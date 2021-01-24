#include <stdio.h>
#include <stdlib.h>
#include "timeformatter.h"

char *timeFormatter(double sec)
{
    int millis = sec * 1000;
    int day = (int)(millis / (1000 * 60 * 60 * 24));
    int hour = (int)((millis / (1000 * 60 * 60))) % 24;
    int minute = (int)((millis / (1000 * 60))) % 60;
    int second = (int)((millis / 1000)) % 60;
    int millisSec = (int)(millis) % 1000;

    char *timeformat = (char *)malloc(sizeof(char)*256);
    if (day > 0) {
        sprintf(timeformat, "%d day + %02d:%02d:%02d:%03d", day, hour, minute, second, millisSec);
    } else {
        sprintf(timeformat, "%02d:%02d:%02d:%03d", hour, minute, second, millisSec);
    }

    return timeformat;
}

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Function prototype declaration */
#include "mylib.h"

#define MALLOC_STR_SIZE 1024

void err_exit(char* message)
{
    fprintf(stderr, "%s", message);
    exit(-1);
}

int p_char_array_free(char* str_array[], int size)
{
    for (int i = size - 1; i >= 0; --i)
        free(str_array[i]);

    return size;
}

int split(char *str, const char *delim, char *outlist[], int outlist_maxlength)
{
    char *pos1, *pos2;
    char *temp_str, *dst;
    int cnt = 0;

    pos1 = str;
    pos2 = strstr( pos1, delim );
    while( pos2 != NULL) {
        if (cnt >= outlist_maxlength - 1)
            err_exit("split() outlist_size error!\n");

        if ((temp_str = malloc(sizeof(char) * MALLOC_STR_SIZE)) == NULL)
            err_exit("malloc failed in split().\n");

        dst = temp_str;

        while ( pos1 < pos2 )
            *dst++ = *pos1++;
        *dst = '\0';

        outlist[cnt++] = temp_str;
        pos1 = pos2 + strlen(delim);
        pos2 = strstr( pos1, delim );
    }

    if ((temp_str = malloc(sizeof(char) * MALLOC_STR_SIZE)) == NULL)
        err_exit("malloc failed in split().\n");

    dst = temp_str;

    while ( *pos1 != '\0')
        *dst++ = *pos1++;
    *dst = '\0';
    outlist[cnt++] = temp_str;

    return cnt;
}

void replace(char *buf, const char *str1, const char *str2)
{
    char tmp[1024 + 1];
    char *p;

    while ((p = strstr(buf, str1)) != NULL) {
        if (*str2 != '\0') {
            *p = '\0';
            p += strlen(str1);
            strcpy(tmp, p);
            strcat(buf, str2);
            strcat(buf, tmp);
        } else {
            char *src, *dst;
            src = dst = p;

            *src = '\0';
            dst += strlen(str1);
            while (*dst != '\0') {
                *src++ = *dst++;
            }
            *src = '\0';
            p += strlen(str1);
        }
    }
}

int trim(char *s)
{
    int i;
    int count = 0;

    if ( s == NULL ) { /* yes */
        return -1;
    }

    if ((i = strlen(s)) <= 0) {
        return 0;
    }

    while (i > 0 && (s[i] == '\0' || s[i] == ' ' || s[i] == '\n' || s[i] == '\r')) {
        i--;
        count++;
    }

    if (i == 0) {
        s[0] = '\0';
        return count;
    }

    s[i + 1] = '\0';

    i = 0;
    while ( s[i] != '\0' && s[i] == ' ' )
        i++;

    if (i > 0)
        strcpy(s, &s[i]);

    return i + count;
}

int str_to_int_array(char* str_nums[], int nums[], int length)
{
    int i;
    for (i = 0; i < length; ++i)
        nums[i] = strtol(str_nums[i], NULL, 10);
    return i;
}

void output_int_array(int nums[], int length)
{
    printf("[");
    for (int i = 0; i < length; ++i) {
        if (i == 0)
            printf("%d", nums[i]);
        else
            printf(",%d", nums[i]);
    }
    printf("]\n");
}

#include <stdio.h>
#include <time.h>

int main()
{
    struct timespec time_start, time_end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &time_start);

    // 総当たり検索実行
    printf("Hit Enter Key to Continue.\n");
    getchar();

    clock_gettime(CLOCK_MONOTONIC_RAW, &time_end);

    // result print.
    printf("time_start.time_t = %ld, time_start.tv_nsec = %ld\n", time_start.tv_sec, time_start.tv_nsec);
    printf("time_end.time_t   = %ld, time_end.tv_nsec   = %ld\n", time_end.tv_sec,   time_end.tv_nsec);

    double time_start_dbl = (double)time_start.tv_sec + (double)time_start.tv_nsec/(10e9);
    double time_end_dbl   = (double)time_end.tv_sec   + (double)time_end.tv_nsec/(10e9);
    double totalSeconds = time_end_dbl - time_start_dbl;
    printf("time_start_dbl = %f\n", time_start_dbl);
    printf("time_end_dbl   = %f\n", time_end_dbl);
    printf("totatlSeconds  = %f\n", totalSeconds);

    printf("Total Execute time ... %.0f ms\n\n", totalSeconds*1000.0);
}

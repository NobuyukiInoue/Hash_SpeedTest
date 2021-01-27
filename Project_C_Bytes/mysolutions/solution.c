#include "mylib.h"
#include "solution.h"
#include "compute_hash_common.h"
#include "search_cleartext_bytes.h"
#include "timeformatter.h"

int solution_start(char *openFileName, int threadCount, int searchMaxLength, int searchMode, bool enableMultiThread, bool enableDebug)
{
    // File Open
    FILE *fp;
    fp = fopen(openFileName, "r");
    if (fp == NULL) {
        printf("%s Open Failed...\n", openFileName);
        return -1;
    }

    int flds_max_length = 2;
    char *flds[flds_max_length];
    const int fgets_MAX = 65535;
    char line[fgets_MAX];
    while ((fgets(line, fgets_MAX - 1, fp)) != NULL) {
        ml_trim(line);
        if (*line == '\0')
            continue;
        remove_comment(line);
        if (strchr(line, ':') != NULL) {
            int flds_length = ml_split(line, ":", flds, flds_max_length);
            if (flds_length > 0) {
                /*
                for (int i = 0; i < flds_max_length; i++) {
                    printf("flds[%d] = %s\n", i, flds[i]);
                }
                */
                break;
            }
        }
    }
    fclose(fp);

    time_t timer;
    struct tm *local;

    char *algorithm = flds[0];
    char *target_hashed_text = flds[1];

    /* 現在時刻を取得 */
    timer = time(NULL);
    local = localtime(&timer); /* 地方時に変換 */

    printf("=====================================================================================\n");
    printf("Date               : %4d/%02d/%02d %02d:%02d:%02d\n",
            local->tm_year + 1900,
            local->tm_mon + 1,
            local->tm_mday,
            local->tm_hour,
            local->tm_min,
            local->tm_sec);
    printf("algorithm          : %s\n", algorithm);
    printf("target Hashed Text : %s\n", target_hashed_text);
    printf("Collation type     : byte[]\n");
    if (enableMultiThread) {
        printf("thread count       : %d\n", threadCount);
    } else {
        printf("multithread        : %s\n", boolToCharArray(enableMultiThread));
    }
    printf("search max length  : %d\n", searchMaxLength);
    printf("=====================================================================================\n");

    struct timespec time_start, time_end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &time_start);

    // 総当たり検索実行
    search(target_hashed_text, algorithm, threadCount, searchMaxLength, searchMode, enableMultiThread, enableDebug);

    free(flds[1]);
    free(flds[0]);

    clock_gettime(CLOCK_MONOTONIC_RAW, &time_end);

    // 経過時間の出力
    double time_start_dbl = (double)time_start.tv_sec + (double)time_start.tv_nsec/(10e9);
    double time_end_dbl   = (double)time_end.tv_sec   + (double)time_end.tv_nsec/(10e9);
    double totalSeconds = time_end_dbl - time_start_dbl;

    printf("Total Execute time ... %.0f ms\n\n", totalSeconds*1000);

    return 0;
}

void search(char *target_hashed_text, char *algorithm, int threadMax, int ClearTextMaxLength, int searchMode, bool enableMuiltiThread, bool enableDebug)
{
    // 使用するスレッド数の指定チェック
    if ((threadMax != 1)
    && (threadMax != 2)
    && (threadMax != 4)
    && (threadMax != 8)
    && (threadMax != 16)) {
        return;
    }

    ml_replace(algorithm, "-", "");
    char *algorithm_upper = ml_toUpper(algorithm);
    int algorithm_Index;

    if (strcmp(algorithm_upper, "MD5") == 0)
        algorithm_Index = 0;
    else if (strcmp(algorithm_upper, "SHA1") == 0)
        algorithm_Index = 1;
    else if (strcmp(algorithm_upper, "SHA256") == 0)
        algorithm_Index = 2;
    else if (strcmp(algorithm_upper, "SHA386") == 0)
        algorithm_Index = 3;
    else if (strcmp(algorithm_upper, "SHA512") == 0)
        algorithm_Index = 4;
    else if (strcmp(algorithm_upper, "RIPED160") == 0)
        algorithm_Index = 5;
    else
        algorithm_Index = 2;  // default .. "SHA256"

    if (enableDebug)
        printf("algorithm_upper = %s, algorithm_Index = %d\n", algorithm_upper, algorithm_Index);

    // char algorithm_upper[]を開放
    free(algorithm_upper);

    // １文字から指定した文字列長まで検索する。
    for (int target_strLength = 1; target_strLength <= ClearTextMaxLength; target_strLength++) {
        struct timespec time_sectionStart, time_sectionEnd;

        // 各種変数のセット
        init_searchClearTextBytes(algorithm_Index, target_hashed_text, target_strLength, threadMax, 0, enableMuiltiThread, enableDebug);

        // 文字数iでの総当たり平文検索開始時刻を保存
        clock_gettime(CLOCK_MONOTONIC_RAW, &time_sectionStart);

        //---------------------------------------------------------------------//
        // 文字数target_strLengthでの総当たり平文検索を実行し、結果を取得
        //---------------------------------------------------------------------//
        char *answerStr = get_clearText(threadMax, target_strLength);

        // 総当たり平文検索終了時刻との差を取得
        clock_gettime(CLOCK_MONOTONIC_RAW, &time_sectionEnd);

        //---------------------------------------------------------------------//
        // 文字数target_strLengthでの総当たり平文検索終了
        //---------------------------------------------------------------------//
        double time_sectionStart_dbl = (double)time_sectionStart.tv_sec + (double)time_sectionStart.tv_nsec/(10e9);
        double time_sectionEnd_dbl   = (double)time_sectionEnd.tv_sec   + (double)time_sectionEnd.tv_nsec/(10e9);
        double totalSeconds = time_sectionEnd_dbl - time_sectionStart_dbl;

        char *tm = timeFormatter(totalSeconds);
        if (answerStr != NULL) {
            printf("元の文字列が見つかりました！\n"
                   "\n"
                   "結果 = %s\n"
                   "\n"
                   "解析時間 = %s ( %f [s] )\n", answerStr, tm, totalSeconds);

            /* スレッド未終了時の状態で解放するとSegmentation faultが発生するので、そのまま終了処理へ */
            // search_cleartext_bytes.cの作業用配列を解放
         // free_arrays();
            break;
        } else {
            printf("%s ... %d文字の組み合わせ照合終了\n", tm, target_strLength);
        }
        free(tm);
    }
}

void remove_comment(char *targetStr)
{
    for (int i = 0; *(targetStr + i) != '\0'; i++) {
        if (*(targetStr + i) =='/' && *(targetStr + i + 1) =='/')
            *(targetStr + i) = '\0';
        if (*(targetStr + i) =='#')
            *(targetStr + i) = '\0';
    }
}

void print_digest(unsigned char *digest)
{
    for (int i = 0; i < strlen(digest); ++i) {
        printf("%x", digest[i]);
    }
    printf("\n");
}

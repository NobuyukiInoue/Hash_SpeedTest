#include <stdlib.h>
#include <time.h>

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
                for (int i = 0; i < flds_max_length; i++) {
                    printf("flds[%d] = %s\n", i, flds[i]);
                }
                break;
            }
        }
    }
    fclose(fp);

    char *algorithm = flds[0];
    char *target_hashed_text = flds[1];

    /* hash sample */
    /*
    unsigned char *digest = compute_hash_common(2, "Hello");
    print_digest(digest);
    */
    time_t timer;
    struct tm *local;

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

    clock_t time_start = clock();

    // 総当たり検索実行
    search(target_hashed_text, algorithm, threadCount, searchMaxLength, searchMode, enableMultiThread, enableDebug);

    clock_t time_end = clock();

    // result print.
    printf("Total Execute time ... %.0f ms\n\n", 1000*(double)(time_end - time_start)/CLOCKS_PER_SEC);

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
    int Algorithm_Index;

    if (strcmp(algorithm_upper, "MD5") == 0)
        Algorithm_Index = 0;
    else if (strcmp(algorithm_upper, "SHA1") == 0)
        Algorithm_Index = 1;
    else if (strcmp(algorithm_upper, "SHA256") == 0)
        Algorithm_Index = 2;
    else if (strcmp(algorithm_upper, "SHA386") == 0)
        Algorithm_Index = 3;
    else if (strcmp(algorithm_upper, "SHA512") == 0)
        Algorithm_Index = 4;
    else if (strcmp(algorithm_upper, "RIPED160") == 0)
        Algorithm_Index = 5;
    else
        Algorithm_Index = 2;  // default .. "SHA256"

    // 現在の時刻を取得
    clock_t time_start = clock();

    // 結果の初期化
    char* answerStr = NULL;

    // １文字から指定した文字列長まで検索する。
    for (int target_strLength = 1; target_strLength <= ClearTextMaxLength; target_strLength++) {

        // 各種変数のセット
        init_searchClearTextBytes(Algorithm_Index, target_hashed_text, target_strLength, threadMax, 0, enableMuiltiThread, enableDebug);

        // 文字数iでの総当たり平文検索開始時刻を保存
        clock_t startTime = clock();

        //---------------------------------------------------------------------//
        // 文字数iでの総当たり平文検索開始
        //---------------------------------------------------------------------//
        answerStr = get_clearText(threadMax, target_strLength);

        // 総当たり平文検索終了時刻との差を取得
        clock_t endTime = clock();

        // 平文作業用配列を解放
    //  disp_resultStrArray();
        free_arrays();

        //---------------------------------------------------------------------//
        // 文字数iでの総当たり平文検索終了
        //---------------------------------------------------------------------//
        double totalSeconds = (double)(endTime - startTime)/CLOCKS_PER_SEC; 
        if (answerStr != NULL) {
            printf("元の文字列が見つかりました！\n"
                   "\n"
                   "結果 = %s\n"
                   "\n"
                   "解析時間 = %s ( %f [s] )\n", answerStr, timeFormatter(totalSeconds), totalSeconds);
            break;
        } else {
            printf("%s ... %d文字の組み合わせ照合終了\n", timeFormatter(totalSeconds), target_strLength);
        }
    }

    if (answerStr == NULL) {
        // 見つからなかった場合
        printf("見つかりませんでした。\n");
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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <math.h>
#include <time.h>
#include "mysolutions/mylib.h"
#include "mysolutions/solution.h"

void printMsgAndExit(char *argv0);

int main(int argc, char* argv[])
{
    if (argc < 3) {
        printf("Usage %s <testdata.txt> <thread_count> [enableMultiThread] [searchMaxLength] [debug]\n", argv[0]);
        return -1;
    }

    // File Open
    FILE *fp;
    fp = fopen(argv[1], "r");
    if (fp == NULL) {
        printf("%s Open Failed...\n", argv[1]);
        return -1;
    }

    char *openFileName = argv[1];
    int threadCount = strtol(argv[2], NULL, 10);

    // スレッド数が定義値かどうかのチェック
    if (threadCount != 1 &&
        threadCount != 2 &&
        threadCount != 4 &&
        threadCount != 8 &&
        threadCount != 16) {
        printf("%s ... Please select threadCount from 1, 2, 4, 8, 16\n", argv[2]);
        return -1;
    }

    // マルチスレッド処理／直列処理の選択
    bool enableMultiThread = true;
    if (argc > 3) {
        char *workStr = ml_toUpper(argv[3]);
        if (strcmp(workStr, "TRUE") == 0) {
            enableMultiThread = true;
        } else if (strcmp(workStr, "FALSE") == 0) {
            enableMultiThread = false;
        } else {
            printf("\"%s\" is Invalid.\n", argv[3]);
            printf("use_mutiThread ... [TRUE | FALSE]\n");
            printMsgAndExit(argv[0]);
        }
    }

    // 検索する最大文字列長の指定チェック
    int searchMaxLength = 256;
    if (argc > 4) {
        searchMaxLength = strtol(argv[4], NULL, 10);
        if (searchMaxLength < 1 || searchMaxLength > 255) {
            printf("argv[4] ... %s is not 0 - 255\n", argv[4]);
            return -1;
        }
    } else {
        searchMaxLength = 256;
    }

    // デバッグ出力の有効／無効
    bool enableDebug = false;
    if (argc > 5) {
        char *workStr = ml_toUpper(argv[5]);
        if (strcmp(workStr, "TRUE") == 0) {
            enableDebug = true;
        } else if (strcmp(workStr, "FALSE") == 0) {
            enableDebug = false;
        } else {
            printf("\"%s\" is Invalid.\n", argv[5]);
            printf("enableDebug ... [TRUE | FALSE]\n");
            printMsgAndExit(argv[0]);
        }
    }

    printf("openFileName = %s, threadCount = %d, searchMaxLength = %d, enableMultiThread = %s, enableDebug = %s\n", openFileName, threadCount, searchMaxLength, boolToCharArray(enableMultiThread), boolToCharArray(enableDebug));
    solution_start(openFileName, threadCount, searchMaxLength, 0, enableMultiThread, enableDebug);

    return 0;
}

// 使用方法を表示して終了
void printMsgAndExit(char *argv0)
{
    printf("\nUsage: %s <testdata.txt> <thread_count> [enableMultiThread] [searchMaxLength] [debug]", argv0);
    exit(-1);
}

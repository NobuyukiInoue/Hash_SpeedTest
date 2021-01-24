#include <pthread.h>
#include <stdio.h>
#include "search_cleartext_bytes.h"

/// スレッド数上限（chr(), srcStr()の要素数）
int thread_MAX;

/// 元の文字列（平文）候補を生成し、ハッシュ文字列と比較処理を行うクラス
/// デバッグ出力の可否
bool output_clearTextList;

/// 処理済み平文の出力用文字列（デバッグ用）
char **clearTextList;
int clearTextListIndex;

/// マルチスレッド処理の可否
bool useMultiThread;

/// 各スレッド処理終了時結果文字列（２次元配列）
char **resultStrArray;

/// 検索対象のハッシュ後文字列
char *targetHashedBytes;

/// 元の文字列の候補（２次元配列）
char **srcStr;

/// 元の文字列の候補（代入前）（２次元配列）
char **chr;

/// 選択したスレッド数のインデックス番号
int selectIndex;

/// 検索対象文字のコードを格納する配列
char *targetChars;

/// 検索範囲先頭文字（２次元配列）
int **chrStart;

/// 検索範囲末尾文字（２次元配列）
int **chrEnd;

/// 選択したアルゴリズムのインデックス番号
int Algorithm_Index;

void init_searchClearTextBytes(int alg_index, char *targetStr, int current_strLen, int threadMax, int mode, bool use_multiThread, bool use_debug)
{
    thread_MAX = threadMax;
    output_clearTextList = use_debug;
    useMultiThread = use_multiThread;

    srcStr = (char **)malloc(sizeof(char *)*thread_MAX);
    chr = (char **)malloc(sizeof(char *)*thread_MAX);
    for (int i = 0; i < thread_MAX; i++) {
            srcStr[i] = (char *)malloc(sizeof(char)*(current_strLen + 1));
            chr[i] = (char *)malloc(sizeof(char)*(current_strLen + 1));
    }

    for (int i = 0; i < thread_MAX; i++) {
        int j;
        for (j = 0; j < current_strLen; j++) {
            chr[i][j] = ' ';
        }
        chr[i][j] = '\0';
    }

    // 選択したアルゴリズムのインデックス番号
    Algorithm_Index = alg_index;

    // 選択したスレッド数のインデックス番号の指定（配列の選択)
    switch (thread_MAX) {
    case 1:
        selectIndex = 0;
        break;
    case 2:
        selectIndex = 1;
        break;
    case 4:
        selectIndex = 2;
        break;
    case 8:
        selectIndex = 3;
        break;
    case 16:
        selectIndex = 4;
        break;
    default:
        printf("threadMax = %d, threadMax is Invalid...", threadMax);
        exit(0);
        break;
    }

    clearTextList = (char **)malloc(sizeof(char *)*pow((0x7f - 0x20), current_strLen));
    clearTextListIndex = 0;

    // ハッシュ後の検索対象文字列をセット
    targetHashedBytes = (char *)malloc(sizeof(char)*(strlen(targetStr)/2));
    for (int i = 0; i < strlen(targetStr); i += 2) {
        targetHashedBytes[i/2] = (char)((charToHex(targetStr[i])*16) + charToHex(targetStr[i + 1]));
    }

    // 検索範囲配列の初期化
    init_targetChars(mode);
}

//-----------------------------------------------------------------------------//
// 検索対象文字を配列にセットする。
//-----------------------------------------------------------------------------//
void init_targetChars(int mode)
{
    // 検索範囲配列の初期化
    init_chr_StartEnd();

    switch (mode) {
        case 0: {
            // 英数文字および記号が対象のとき
            //targetChars = new int[0xff - 0x00];
            targetChars = (char *)malloc(sizeof(char)*(0x7f - 0x20));

            int i = 0;
            for (char num = 0x20; num < 0x7f; num++, i++) {
                targetChars[i] = num;
            }
            break;
        }
        case 1: {
            // 英数文字のみが対象のとき
            targetChars = (char *)malloc(sizeof(char)*(('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1));

            int i = 0;
            for (char num = '0'; num <= '9'; num++, i++) {
                targetChars[i] = num;
            }

            for (char num = 'A'; num <= 'Z'; num++, i++) {
                targetChars[i] = num;
            }

            for (char num = 'a'; num <= 'z'; num++, i++) {
                targetChars[i] = num;
            }
            break;
        }
    }

    // 検索範囲配列の初期化
    set_chr_StartEnd();
}

//-----------------------------------------------------------------------------//
// 検索範囲配列の初期化
//-----------------------------------------------------------------------------//
void init_chr_StartEnd()
{
    chrStart = (int **)malloc(sizeof(int *)*5);
    chrEnd = (int **)malloc(sizeof(int *)*5);

    chrStart[0] = (int *)malloc(sizeof(int)*1);
    chrStart[1] = (int *)malloc(sizeof(int)*2);
    chrStart[2] = (int *)malloc(sizeof(int)*4);
    chrStart[3] = (int *)malloc(sizeof(int)*8);
    chrStart[4] = (int *)malloc(sizeof(int)*16);

    chrEnd[0] = (int *)malloc(sizeof(int)*1);
    chrEnd[1] = (int *)malloc(sizeof(int)*2);
    chrEnd[2] = (int *)malloc(sizeof(int)*4);
    chrEnd[3] = (int *)malloc(sizeof(int)*8);
    chrEnd[4] = (int *)malloc(sizeof(int)*16);
}

//-----------------------------------------------------------------------------//
// 検索範囲配列の解放
//-----------------------------------------------------------------------------//
void free_chr_StartEnd()
{
    for (int i = 0; i < 5; i++) {
        free(chrEnd[i]);
        free(chrStart[i]);
    }
    free(chrEnd);
    free(chrStart);
}

//-----------------------------------------------------------------------------//
// 各スレッドごとの対象範囲のセット
//-----------------------------------------------------------------------------//
void set_chr_StartEnd()
{
    //-------------------------------------------------------------------------//
    // スレッド数が１のときの開始・終了文字
    //-------------------------------------------------------------------------//
    chrStart[0][0] = 0;
    chrEnd[0][0] = strlen(targetChars);

    //-------------------------------------------------------------------------//
    // スレッド数が２（配列インデックス=1）のときの開始・終了文字
    //-------------------------------------------------------------------------//
    chrStart[1][0] = 0;
    chrStart[1][1] = strlen(targetChars) / 2;
    chrEnd[1][0] = chrStart[1][1];
    chrEnd[1][1] = strlen(targetChars);

    //-------------------------------------------------------------------------//
    // スレッド数が４（配列インデックス=2）のときの開始・終了文字
    //-------------------------------------------------------------------------//
    chrStart[2][0] = 0;
    chrStart[2][1] = 1 * strlen(targetChars) / 4;
    chrStart[2][2] = 2 * strlen(targetChars) / 4;
    chrStart[2][3] = 3 * strlen(targetChars) / 4;
    chrEnd[2][0] = chrStart[2][1];
    chrEnd[2][1] = chrStart[2][2];
    chrEnd[2][2] = chrStart[2][3];
    chrEnd[2][3] = strlen(targetChars);

    //-------------------------------------------------------------------------//
    // スレッド数が８（配列インデックス=3）のときの開始・終了文字
    //-------------------------------------------------------------------------//
    chrStart[3][0] = 0;
    chrStart[3][1] = 1 * strlen(targetChars) / 8;
    chrStart[3][2] = 2 * strlen(targetChars) / 8;
    chrStart[3][3] = 3 * strlen(targetChars) / 8;
    chrStart[3][4] = 4 * strlen(targetChars) / 8;
    chrStart[3][5] = 5 * strlen(targetChars) / 8;
    chrStart[3][6] = 6 * strlen(targetChars) / 8;
    chrStart[3][7] = 7 * strlen(targetChars) / 8;
    chrEnd[3][0] = chrStart[3][1];
    chrEnd[3][1] = chrStart[3][2];
    chrEnd[3][2] = chrStart[3][3];
    chrEnd[3][3] = chrStart[3][4];
    chrEnd[3][4] = chrStart[3][5];
    chrEnd[3][5] = chrStart[3][6];
    chrEnd[3][6] = chrStart[3][7];
    chrEnd[3][7] = strlen(targetChars);

    //-------------------------------------------------------------------------//
    // スレッド数が１６（配列インデックス=4）のときの開始・終了文字
    //-------------------------------------------------------------------------//
    chrStart[4][0] = 0;
    chrStart[4][1] = 1 * strlen(targetChars) / 16;
    chrStart[4][2] = 2 * strlen(targetChars) / 16;
    chrStart[4][3] = 3 * strlen(targetChars) / 16;
    chrStart[4][4] = 4 * strlen(targetChars) / 16;
    chrStart[4][5] = 5 * strlen(targetChars) / 16;
    chrStart[4][6] = 6 * strlen(targetChars) / 16;
    chrStart[4][7] = 7 * strlen(targetChars) / 16;
    chrStart[4][8] = 8 * strlen(targetChars) / 16;
    chrStart[4][9] = 9 * strlen(targetChars) / 16;
    chrStart[4][10] = 10 * strlen(targetChars) / 16;
    chrStart[4][11] = 11 * strlen(targetChars) / 16;
    chrStart[4][12] = 12 * strlen(targetChars) / 16;
    chrStart[4][13] = 13 * strlen(targetChars) / 16;
    chrStart[4][14] = 14 * strlen(targetChars) / 16;
    chrStart[4][15] = 15 * strlen(targetChars) / 16;
    chrEnd[4][0] = chrStart[4][1];
    chrEnd[4][1] = chrStart[4][2];
    chrEnd[4][2] = chrStart[4][3];
    chrEnd[4][3] = chrStart[4][4];
    chrEnd[4][4] = chrStart[4][5];
    chrEnd[4][5] = chrStart[4][6];
    chrEnd[4][6] = chrStart[4][7];
    chrEnd[4][7] = chrStart[4][8];
    chrEnd[4][8] = chrStart[4][9];
    chrEnd[4][9] = chrStart[4][10];
    chrEnd[4][10] = chrStart[4][11];
    chrEnd[4][11] = chrStart[4][12];
    chrEnd[4][12] = chrStart[4][13];
    chrEnd[4][13] = chrStart[4][14];
    chrEnd[4][14] = chrStart[4][15];
    chrEnd[4][15] = strlen(targetChars);
}

//-------------------------------------------------------------------//
// 開始位置、終了位置の確認
//-------------------------------------------------------------------//
void display_chrStartEnd()
{
    printf("strlen(targetChars) = %lu\n", strlen(targetChars));

    for (int rows = 0; rows < sizeof(chrStart); rows++) {
        for (int th = 0; th < sizeof(chrStart[rows]); th++) {
            printf("chrStart[%d][%d] = %c\n", rows, th, chrStart[rows][th]);
            printf("chrEnd[%d][%d] = %c\n", rows, th, chrEnd[rows][th]);
        }
    }

    for (int i = 0; i < strlen(targetChars); i++) {
        printf("targetChars[%d] = %c", i, targetChars[i]);
    }
}

//-------------------------------------------------------------------//
// 元の文字列総当たり検索
//-------------------------------------------------------------------//
char *get_clearText(int threadMax, int target_strLength)
{
    //-------------------------------------------------------------------------//
    // 平文が""かどうかを判定する。
    //-------------------------------------------------------------------------//
    if (bytesEquals(targetHashedBytes, compute_hash_common(Algorithm_Index, ""))) {
        return ("");
    }

    //-------------------------------------------------------------------------//
    // 平文が１文字以上の文字列の場合
    //-------------------------------------------------------------------------//
    resultStrArray = (char **)malloc(sizeof(char)*threadMax);
    for (int i = 0; i < threadMax; i++) {
        resultStrArray[i] = (char *)malloc(sizeof(char)*(target_strLength + 1));
    //  printf("resultStrarray[%d]...%lx\n", i, resultStrArray[i]);
    }

    if (useMultiThread) {
        //---------------------------------------------------------------------//
        // マルチスレッド処理
        //---------------------------------------------------------------------//
        pthread_t th_table[threadMax];
        struct thread_args th_data[threadMax];

        for (int threadNum = 0; threadNum < threadMax; threadNum++) {
            th_data[threadNum].threadNum = threadNum;

            // スレッドを生成
            pthread_create(&th_table[threadNum], NULL, func_thread, &th_data[threadNum]);
        }
            
        for (int threadNum = 0; threadNum < threadMax; threadNum++) {
            // スレッドの終了を待機
            pthread_join(th_table[threadNum], NULL);
        }

    } else {
        //---------------------------------------------------------------------//
        // 直列実行
        //---------------------------------------------------------------------//
        for (int threadNum = 0; threadNum < threadMax; threadNum++) {
            // 指定したアルゴリズムにてハッシュ値を生成する。
            if (get_NextClearText_Group_All(threadNum, 0)) {
                //-----------------------------------------------------------//
                // 同じハッシュ値が生成できる元の文字列が見つかった場合
                //-----------------------------------------------------------//
                strcpy(resultStrArray[threadNum], srcStr[threadNum]);
            } else {
                strcpy(resultStrArray[threadNum], "");
            }
        }
    }

    //-------------------------------------------------------------------------//
    // 指定文字数での結果報告
    //-------------------------------------------------------------------------//
    while (true) {
        int resultCount = 0;

        for (int i = 0; i < threadMax; i++) {
        //  printf("result ... resultStrarray[%d]...%lx\n", i, resultStrArray[i]);

            if (resultStrArray[i] != NULL) {
                if (strlen(resultStrArray[i]) > 0) {
                    // デバッグ用出力
                    if (output_clearTextList)
                        save_clearTextList();

                    // いずれかのスレッドが文字列を返してきた場合（見つかった場合）
                    char *answerStr = (char *)malloc(sizeof(char)*(strlen(resultStrArray[i]) + 1));
                    strcpy(answerStr, resultStrArray[i]);
                    return answerStr;
                } else {
                    resultCount++;

                    if (resultCount >= threadMax) {
                        // デバッグ用出力
                        if (output_clearTextList)
                            save_clearTextList();

                        // すべて""だった場合（見つからなかった場合）
                        return NULL;
                    }
                }
            }
        }
        return NULL;
    }
}

void *func_thread(void *args)
{
    struct thread_args *th_data = (struct thread_args *)args;
    int threadNum = th_data->threadNum;

    // 指定したアルゴリズムにてハッシュ値を生成する。
    if (get_NextClearText_Group_All(threadNum, 0) == true) {
        //-----------------------------------------------------------//
        // 同じハッシュ値が生成できる元の文字列が見つかった場合
        //-----------------------------------------------------------//
        strcpy(resultStrArray[threadNum], srcStr[threadNum]);
    } else {
        strcpy(resultStrArray[threadNum], "");
    }
}
//-------------------------------------------------------------------//
// 検索した平文リストのファイルへの保存
//-------------------------------------------------------------------//
void save_clearTextList()
{
/*
    StreamWriter sw = new StreamWriter("ClearTextList_" + srcStr[0].Length + ".txt" , false);
    sw.Write(clearTextList);
    sw.Close();
*/
}

//-------------------------------------------------------------------//
// 当該階層の平文候補を生成しハッシュ値と比較する。
// 見つからなければ次の階層へ。
//-------------------------------------------------------------------//
bool get_NextClearText_Group_All(int threadNum, int target_strLength)
{
    // 文字列の長さの上限を超えた場合は中止する。
    int temp = strlen(chr[threadNum]);
    if (target_strLength > strlen(chr[threadNum]) - 1) {
        return false;
    }

    strcpy(srcStr[threadNum], chr[threadNum]);

    // まずは文字列長target_strLengthの候補をチェック
    for (int index = chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++) {
        chr[threadNum][target_strLength] = targetChars[index];
        srcStr[threadNum][target_strLength] = chr[threadNum][target_strLength];

        // デバッグ用出力
        if (output_clearTextList) {
            clearTextList[clearTextListIndex] = (char *)malloc(sizeof(char)*(target_strLength + 1));
            sprintf(clearTextList[clearTextListIndex], "\"%s\"\r\n", srcStr[threadNum]);
            clearTextListIndex++;
        }

        // for debug.
    //  printf("srcStr[%d] = \"%s\"\n", threadNum, srcStr[threadNum]);

        // 指定したアルゴリズムにてハッシュ値を生成する。
        if (bytesEquals(targetHashedBytes, compute_hash_common(Algorithm_Index, srcStr[threadNum]))) {
            return true;
        }
    }

    // 文字列長target_strLength + 1の候補をチェック
    for (int index = chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++) {
        chr[threadNum][target_strLength] = targetChars[index];

        if (get_NextClearText_Group_All_level2(threadNum, target_strLength + 1)) {
            return true;
        }
    }

    return false;
}

//-------------------------------------------------------------------//
// 当該階層の平文候補を生成しハッシュ値と比較する。
// 見つからなければ次の階層へ。
//-------------------------------------------------------------------//
bool get_NextClearText_Group_All_level2(int threadNum, int target_strLength)
{
    // 文字列の長さの上限を超えた場合は中止する。
    int temp = strlen(chr[threadNum]);
    if (target_strLength > strlen(chr[threadNum]) - 1) {
        return false;
    }

    strcpy(srcStr[threadNum], chr[threadNum]);

    // まずは文字列長target_strLengthの候補をチェック
    for (int index = chrStart[0][0]; index < chrEnd[0][0]; index++) {
        chr[threadNum][target_strLength] = targetChars[index];
        srcStr[threadNum][target_strLength] = chr[threadNum][target_strLength];

        // デバッグ用出力
        if (output_clearTextList) {
            clearTextList[clearTextListIndex] = (char *)malloc(sizeof(char)*(target_strLength + 1));
            sprintf(clearTextList[clearTextListIndex], "\"%s\"\r\n", srcStr[threadNum]);
            clearTextListIndex++;
        }

        // for debug.
    //  printf("srcStr[%d] = \"%s\"\n", threadNum, srcStr[threadNum]);

        // 指定したアルゴリズムにてハッシュ値を生成する。
        if (bytesEquals(targetHashedBytes, compute_hash_common(Algorithm_Index, srcStr[threadNum]))) {
            return true;
        }
    }

    // 文字列長target_strLength + 1の候補をチェック
    for (int index = chrStart[0][0]; index < chrEnd[0][0]; index++) {
        chr[threadNum][target_strLength] = targetChars[index];

        if (get_NextClearText_Group_All_level2(threadNum, target_strLength + 1)) {
            return true;
        }
    }

    return false;
}

//-------------------------------------------------------------------//
// 生成した元の文字列候補を表示する。
//-------------------------------------------------------------------//
void display_clearText()
{
    char *clearText[sizeof(srcStr)];

    for (int thread = 0; thread < sizeof(srcStr); thread++) {
        // スレッドごとに途中経過文字列を取得

        clearText[thread] = "";
        for (int i = 0; i < sizeof(srcStr[thread]); i++) {
            clearText[thread][i] = srcStr[thread][i];
        }

        if (clearText[thread][0] != '\0') {
            // 出力先テキストボックスに出力
            printf("スレッド%d: (起動待ち)", thread);
        } else if (resultStrArray[thread][0] == '\0') {
            // 出力先テキストボックスに出力
            printf("スレッド%d: (処理終了)", thread);
        } else {
            int threadCount = sizeof(srcStr);
            //double progress = ((double)(srcStr[thread][0] - targetChars[chrStart[selectIndex][thread]]) / (double)(chrEnd[selectIndex][thread] - chrStart[selectIndex][thread])) * 100;
            double progress = ((double)(get_index_targetChars(srcStr[thread][0]) - get_index_targetChars(targetChars[chrStart[selectIndex][thread]])) / (double)(chrEnd[selectIndex][thread] - chrStart[selectIndex][thread])) * 100;

            // 出力先テキストボックスに出力
            printf("スレッド%d  (%s \% 終了) : \n", thread, clearText[thread]);
        }

        if (thread % 2 == 0) {
            printf("\t");
        } else {
            printf("\n");
        }
    }
}

//-------------------------------------------------------------------//
// 平文生成作業用配列を解放
//-------------------------------------------------------------------//
void free_arrays()
{
    free_clearTextList();
//  free_resultStrArray();
    free_chr_StartEnd();
    free_chr();
    free_srcStr();
}

//-------------------------------------------------------------------//
// resultStrArray()を解放
//-------------------------------------------------------------------//
void free_resultStrArray()
{
    for (int i = thread_MAX - 1; i >= 0; i--) {
        if (resultStrArray[i] != NULL && resultStrArray[i] != 0) {
        //  printf("execute free(resultStrArray[%d])...%lx\n", i, resultStrArray[i]);
            free(resultStrArray[i]);
        }
    }
    free(resultStrArray);
}

//-------------------------------------------------------------------//
// resultStrArray()のアドレスを出力
//-------------------------------------------------------------------//
void disp_resultStrArray()
{
    for (int i = 0; i < thread_MAX; i++) {
        printf("disp resultStrArray[%d])...%lx\n", i, resultStrArray[i]);
    }
}

//-------------------------------------------------------------------//
// clearTextList()を解放
//-------------------------------------------------------------------//
void free_clearTextList()
{
    /*
    for (int i = clearTextListIndex - 1; i >= 0; i--) {
        free(clearTextList[i]);
    }
    */
    free(clearTextList);
}

//-------------------------------------------------------------------//
// chr()を解放
//-------------------------------------------------------------------//
void free_chr()
{
    for (int i = thread_MAX - 1; i >= 0; i--) {
    //  printf("execute free(chr[%d])...%x\n", i, chr[i]);
        free(chr[i]);
    }
    free(chr);
}

//-------------------------------------------------------------------//
// srcStr()を解放
//-------------------------------------------------------------------//
void free_srcStr()
{
    for (int i = thread_MAX - 1; i >= 0; i--) {
    //  printf("execute free(srcStr[%d])...%x\n", i, srcStr[i]);
        free(srcStr[i]);
    }
    free(srcStr);
}

//-------------------------------------------------------------------//
// 指定した文字が、targetChars[]の何番目かを調べる
//-------------------------------------------------------------------//
int get_index_targetChars(char val)
{
    int targetChars_length = strlen(targetChars);
    for (int i = 0; i < targetChars_length; i++) {
        if (val == targetChars[i]) {
            return (i);
        }
    }
    return (0);
}

//-------------------------------------------------------------------//
// 16進数文字列を数値に変換する
//-------------------------------------------------------------------//
char charToHex(char s)
{
    if (0x30 <= s && s <= 0x39) {
        return s - 0x30;
    } else if (0x41 <= s && s <= 0x46) {
        return s - 55;
    } else if (0x61 <= s && s <= 0x66) {
        return s - 87;
    }

    printf("charToHex() Error ...\n%c is wrong.\n", s);
    return 0xff;
}

//-------------------------------------------------------------------//
// ２つのchar配列の内容が一致しているか調べる
//-------------------------------------------------------------------//
bool bytesEquals(char *arr1, char *arr2)
{
    int arr1_length = sizeof(arr1);
    int arr2_length = sizeof(arr2);

    if (arr1_length != arr2_length) {
        return false;
    }

    for (int i = 0; i < arr1_length; i++) {
        if (arr1[i] != arr2[i]) {
            return false;
        }
    }

    return true;
}

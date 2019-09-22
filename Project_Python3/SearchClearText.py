# coding: utf-8

import time
import threading

""" import local liblarys """
import ComputeHash


class SearchClearText:

    def __init__(self, alg_index, targetStr, strLen, threadMax, mode):

        # デバッグ用出力の指定
        #self.output_clearTextList = True
        self.output_clearTextList = False

        # マルチスレッド処理の可否
        self.useMultiThread = True
        #self.useMultiThread = True

        self.clearTextList = ""
        self.srcStr = [0]*threadMax
        self.chr = [0]*threadMax

        for i in range(0, threadMax):
            self.srcStr[i] = [0]*strLen
            self.chr[i] = [0]*strLen

        # 選択したアルゴリズムのインデックス番号
        self.Algorithm_Index = alg_index

        # 選択したスレッド数のインデックス番号の指定（配列の選択)
        if threadMax == 1:
            self.selectIndex = 0
        elif threadMax == 2:
            self.selectIndex = 1
        elif threadMax == 4:
            self.selectIndex = 2
        elif threadMax == 8:
            self.selectIndex = 3
        elif threadMax == 16:
            self.selectIndex = 4
        else:
            print("threadMax = {0:d} threadMax is Invalid...".format(threadMax))
            exit(-1)

        # ハッシュ後の検索対象文字列をセット
        self.target_HashedStr = targetStr

        # 検索範囲配列の初期化
        self.targetChars_Init(mode)

        self.ch = ComputeHash.ComputeHash()

    #-----------------------------------------------------------------------------
    # 検索対象文字を配列にセットする。
    #-----------------------------------------------------------------------------
    def targetChars_Init(self, mode):
        # 検索範囲配列の初期化
        self.chr_StartEnd_Init()

        if mode == 0:
            # 英数文字および記号が対象のとき
            self.targetChars = [0]*(0x7f - 0x20)

            for i, num in enumerate(range(0x20, 0x7f)):
                self.targetChars[i] = num.to_bytes(1, 'big')

        elif mode == 1:
            # 英数文字のみが対象のとき
            self.targetChars = [('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1]

            for i, num in enumerate(range('0', '9' + 1)):
                self.targetChars[i] = num

            for i, num in enumerate(range('A', 'Z' + 1)):
                self.targetChars[i] = num

            for i, num in enumerate(range('a', 'z' + 1)):
                self.targetChars[i] = num

        # 検索範囲配列の初期化
        self.chr_StartEnd_Set()


    #-----------------------------------------------------------------------------
    # 検索範囲配列の初期化
    #-----------------------------------------------------------------------------
    def chr_StartEnd_Init(self):
        self.chrStart = [[]]*5
        self.chrEnd = [[]]*5

        self.chrStart[0] = [0]*1
        self.chrStart[1] = [0]*2
        self.chrStart[2] = [0]*4
        self.chrStart[3] = [0]*8
        self.chrStart[4] = [0]*16

        self.chrEnd[0] = [0]*1
        self.chrEnd[1] = [0]*2
        self.chrEnd[2] = [0]*4
        self.chrEnd[3] = [0]*8
        self.chrEnd[4] = [0]*16


    #-----------------------------------------------------------------------------#
    # 各スレッドごとの対象範囲のセット
    #-----------------------------------------------------------------------------#
    def chr_StartEnd_Set(self):
        #-------------------------------------------------------------------------#
        # スレッド数が１のときの開始・終了文字
        #-------------------------------------------------------------------------#
        self.chrStart[0][0] = 0
        self.chrEnd[0][0] = len(self.targetChars)

        #-------------------------------------------------------------------------#
        # スレッド数が２（配列インデックス=1）のときの開始・終了文字
        #-------------------------------------------------------------------------#
        self.chrStart[1][0] = 0
        self.chrStart[1][1] = len(self.targetChars) // 2
        self.chrEnd[1][0] = self.chrStart[1][1]
        self.chrEnd[1][1] = len(self.targetChars)

        #-------------------------------------------------------------------------#
        # スレッド数が４（配列インデックス=2）のときの開始・終了文字
        #-------------------------------------------------------------------------#
        self.chrStart[2][0] = 0
        self.chrStart[2][1] = 1 * len(self.targetChars) // 4
        self.chrStart[2][2] = 2 * len(self.targetChars) // 4
        self.chrStart[2][3] = 3 * len(self.targetChars) // 4
        self.chrEnd[2][0] = self.chrStart[2][1]
        self.chrEnd[2][1] = self.chrStart[2][2]
        self.chrEnd[2][2] = self.chrStart[2][3]
        self.chrEnd[2][3] = len(self.targetChars)

        #-------------------------------------------------------------------------#
        # スレッド数が８（配列インデックス=3）のときの開始・終了文字
        #-------------------------------------------------------------------------#
        self.chrStart[3][0] = 0
        self.chrStart[3][1] = 1 * len(self.targetChars) // 8
        self.chrStart[3][2] = 2 * len(self.targetChars) // 8
        self.chrStart[3][3] = 3 * len(self.targetChars) // 8
        self.chrStart[3][4] = 4 * len(self.targetChars) // 8
        self.chrStart[3][5] = 5 * len(self.targetChars) // 8
        self.chrStart[3][6] = 6 * len(self.targetChars) // 8
        self.chrStart[3][7] = 7 * len(self.targetChars) // 8
        self.chrEnd[3][0] = self.chrStart[3][1]
        self.chrEnd[3][1] = self.chrStart[3][2]
        self.chrEnd[3][2] = self.chrStart[3][3]
        self.chrEnd[3][3] = self.chrStart[3][4]
        self.chrEnd[3][4] = self.chrStart[3][5]
        self.chrEnd[3][5] = self.chrStart[3][6]
        self.chrEnd[3][6] = self.chrStart[3][7]
        self.chrEnd[3][7] = len(self.targetChars)

        #-------------------------------------------------------------------------#
        # スレッド数が１６（配列インデックス=4）のときの開始・終了文字
        #-------------------------------------------------------------------------#
        self.chrStart[4][0] = 0
        self.chrStart[4][1] = 1 * len(self.targetChars) // 16
        self.chrStart[4][2] = 2 * len(self.targetChars) // 16
        self.chrStart[4][3] = 3 * len(self.targetChars) // 16
        self.chrStart[4][4] = 4 * len(self.targetChars) // 16
        self.chrStart[4][5] = 5 * len(self.targetChars) // 16
        self.chrStart[4][6] = 6 * len(self.targetChars) // 16
        self.chrStart[4][7] = 7 * len(self.targetChars) // 16
        self.chrStart[4][8] = 8 * len(self.targetChars) // 16
        self.chrStart[4][9] = 9 * len(self.targetChars) // 16
        self.chrStart[4][10] = 10 * len(self.targetChars) // 16
        self.chrStart[4][11] = 11 * len(self.targetChars) // 16
        self.chrStart[4][12] = 12 * len(self.targetChars) // 16
        self.chrStart[4][13] = 13 * len(self.targetChars) // 16
        self.chrStart[4][14] = 14 * len(self.targetChars) // 16
        self.chrStart[4][15] = 15 * len(self.targetChars) // 16
        self.chrEnd[4][0] = self.chrStart[4][1]
        self.chrEnd[4][1] = self.chrStart[4][2]
        self.chrEnd[4][2] = self.chrStart[4][3]
        self.chrEnd[4][3] = self.chrStart[4][4]
        self.chrEnd[4][4] = self.chrStart[4][5]
        self.chrEnd[4][5] = self.chrStart[4][6]
        self.chrEnd[4][6] = self.chrStart[4][7]
        self.chrEnd[4][7] = self.chrStart[4][8]
        self.chrEnd[4][8] = self.chrStart[4][9]
        self.chrEnd[4][9] = self.chrStart[4][10]
        self.chrEnd[4][10] = self.chrStart[4][11]
        self.chrEnd[4][11] = self.chrStart[4][12]
        self.chrEnd[4][12] = self.chrStart[4][13]
        self.chrEnd[4][13] = self.chrStart[4][14]
        self.chrEnd[4][14] = self.chrStart[4][15]
        self.chrEnd[4][15] = len(self.targetChars)


    #-------------------------------------------------------------------#
    # 開始位置、終了位置の確認
    #-------------------------------------------------------------------#
    def display_chrStartEnd(self):
        print("\n"
              "len(targetChars) = {0}".format(len(self.targetChars)))

        for rows in range(0, len(self.chrStart)):
            for th in range(0, len(self.chrStart[rows])):
                print("chrStart[{0}][{1}] = {2}".format(rows, th, self.chrStart[rows][th]))
                print("chrEnd[{0}][{1}] = {2}".format(rows, th, self.chrEnd[rows][th]))

        for i in range(0, len(self.targetChars)):
            print("targetChars[{0}] = {1:x}".format(i, self.targetChars[i]))

    #-------------------------------------------------------------------#
    # 元の文字列総当たり検索
    #-------------------------------------------------------------------#
    def Get_ClearText(self, threadMax):
        #-------------------------------------------------------------------------#
        # 平文が""かどうかを判定する。
        #-------------------------------------------------------------------------#
        if self.target_HashedStr == self.ch.ComputeHash_Common(self.Algorithm_Index, "".encode('ascii')):
            return ""

        #-------------------------------------------------------------------------#
        # 各スレッド用の結果格納用配列の初期化
        #-------------------------------------------------------------------------#
        self.resultStr = [None]*threadMax

        #---------------------------------------------------------------------#
        # マルチスレッド処理
        #---------------------------------------------------------------------#
        if self.useMultiThread:
            # マルチスレッド実行
            myThreads = []
            for threadNum in range(0, threadMax):
                myThreads.append(threading.Thread(target = self.thread_func(threadNum)))
                myThreads[threadNum].start()
        else:
            # 直列実行
            for threadNum in range(0, threadMax):
                self.thread_func(threadNum)

        #-------------------------------------------------------------------------#
        # すべてのスレッドが結果を返すまで待機する。
        #-------------------------------------------------------------------------#
        while True:
            resultCount = 0
            time.sleep(0.5)

            for i in range(0, threadMax):
                if self.resultStr[i] != None:
                    if self.resultStr[i] != "":
                        # デバッグ用
                        if self.output_clearTextList:
                            self.save_clearTextList()

                        # いずれかのスレッドが文字列を返してきた場合（見つかった場合）
                        return self.resultStr[i]
                    else:
                        resultCount += 1

                        if resultCount >= threadMax:
                            # デバッグ用
                            if self.output_clearTextList:
                                self.save_clearTextList()

                            # すべて""だった場合（見つからなかった場合）
                            return None

    #-------------------------------------------------------------------#
    # マルチスレッド処理部
    #-------------------------------------------------------------------#
    def thread_func(self, threadNum):
        # 指定したアルゴリズムにてハッシュ値を生成する。
        if self.Get_NextClearText_Group_All(threadNum, 0):
            #-----------------------------------------------------------#
            # 同じハッシュ値が生成できる元の文字列が見つかった場合
            #-----------------------------------------------------------#
            for i in range(0, len(self.srcStr[threadNum])):
                self.resultStr[threadNum] = self.bytes_array_to_str(self.srcStr[threadNum])
        else:
            self.resultStr[threadNum] = ""

    #-------------------------------------------------------------------#
    # 検索した平文リストのファイルへの保存
    #-------------------------------------------------------------------#
    def save_clearTextList(self):
        with open("ClearTextList_" + str(len(self.srcStr[0])) + ".txt", mode='w') as f:
            f.writelines(self.clearTextList)

    #-------------------------------------------------------------------#
    # 当該階層の平文候補を生成しハッシュ値と比較する。
    # 見つからなければ次の階層へ。
    #-------------------------------------------------------------------#
    def Get_NextClearText_Group_All(self, threadNum, i):
        # 文字列の長さの上限を超えた場合は中止する。
        if i > len(self.chr[threadNum]) - 1:
            return False

        self.srcStr[threadNum] = self.chr[threadNum]

        # まずは文字列長iの候補をチェック
        for index in range(self.chrStart[self.selectIndex][threadNum], self.chrEnd[self.selectIndex][threadNum]):
            self.chr[threadNum][i] = self.targetChars[index]
            self.srcStr[threadNum][i] = self.chr[threadNum][i]

            # デバッグ用出力
            if self.output_clearTextList:
                self.clearTextList += "\"" + self.bytes_array_to_str(self.srcStr[threadNum]) + "\"\n"

            # 指定したアルゴリズムにてハッシュ値を生成する。
            if self.target_HashedStr == self.ch.ComputeHash_Common(self.Algorithm_Index, self.bytes_array_to_bytes(self.srcStr[threadNum])):
                return True

        # 文字列長i + 1の候補をチェック
        for index in range(self.chrStart[self.selectIndex][threadNum], self.chrEnd[self.selectIndex][threadNum]):
            self.chr[threadNum][i] = self.targetChars[index]

            if self.Get_NextClearText_Group_All_level2(threadNum, i + 1):
                return True

        return False

    #-------------------------------------------------------------------#
    # 当該階層の平文候補を生成しハッシュ値と比較する。
    # 見つからなければ次の階層へ。
    #-------------------------------------------------------------------#
    def Get_NextClearText_Group_All_level2(self, threadNum, i):
        # 文字列の長さの上限を超えた場合は中止する。
        if i > len(self.chr[threadNum]) - 1:
            return False

        self.srcStr[threadNum] = [0]*(i + 1)

        for col in range(0, i):
            self.srcStr[threadNum][col] = self.chr[threadNum][col]

        # まずは文字列長iの候補をチェック
        for index in range(self.chrStart[0][0], self.chrEnd[0][0]):
            self.chr[threadNum][i] = self.targetChars[index]
            self.srcStr[threadNum][i] = self.chr[threadNum][i]

            # デバッグ用出力
            if self.output_clearTextList:
                self.clearTextList += "\"" + self.bytes_array_to_str(self.srcStr[threadNum]) + "\"\n"

            # 指定したアルゴリズムにてハッシュ値を生成する。
            if self.target_HashedStr == self.ch.ComputeHash_Common(self.Algorithm_Index, self.bytes_array_to_bytes(self.srcStr[threadNum])):
                return True

        # 文字列長i + 1の候補をチェック
        for index in range(self.chrStart[0][0], self.chrEnd[0][0]):
            self.chr[threadNum][i] = self.targetChars[index]

            if self.Get_NextClearText_Group_All_level2(threadNum, i + 1):
                return True

        return False

    #-------------------------------------------------------------------#
    # 生成した元の文字列候補を表示する。
    #-------------------------------------------------------------------#
    def display_ClearText(self):
        clearText = []*len(self.srcStr)

        for thread in range(0, len(self.srcStr)):
            # スレッドごとに途中経過文字列を取得

            clearText[thread] = ""
            for i in range(0, len(self.srcStr[thread])):
                clearText[thread] += self.srcStr[thread][i]

            if clearText[thread].indexOf("\0") >= 0:
                # 出力先テキストボックスに出力
                print("スレッド{0}: (起動待ち)".format(thread))
            elif self.resultStr[thread] == "":
                # 出力先テキストボックスに出力
                print("スレッド{0}: (処理終了)".format(thread))
            else:
                progress = ((self.get_index_targetChars(self.srcStr[thread][0]) - self.get_index_targetChars(self.targetChars[self.chrStart[self.selectIndex][thread]])) / (self.chrEnd[self.selectIndex][thread] - self.chrStart[self.selectIndex][thread])) * 100

                # 出力先テキストボックスに出力
                print("スレッド{0} ({1:.0f}% 終了) : {2}".format(thread, progress, clearText[thread]))

            if thread % 2 == 0:
                print("\t")
            else:
                print()

    #-------------------------------------------------------------------#
    # 指定した文字(byte型)が、targetChars[]の何番目かを調べる
    #-------------------------------------------------------------------#
    def get_index_targetChars(self, val):
        for i in range(0, len(self.targetChars)):
            if val == self.targetChars[i]:
                return i
        return 0


    #-------------------------------------------------------------------#
    # byte[]を文字列に変換する
    #-------------------------------------------------------------------#
    def bytes_array_to_str(self, arr):
        resultStr = ""
        for i in range(len(arr)):
            if arr[i] != 0:
                resultStr += arr[i].decode('ascii')
        return resultStr

    #-------------------------------------------------------------------#
    # byte[]をbyte列に変換する
    #-------------------------------------------------------------------#
    def bytes_array_to_bytes(self, arr):
        result_val = arr[0]
        for i in range(1, len(arr)):
            if arr[i] != 0:
                result_val += arr[i]
        return result_val

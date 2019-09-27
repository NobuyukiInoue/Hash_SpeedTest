# coding: utf-8

import datetime
import time
import re

""" import local liblarys """
from .SearchClearText import SearchClearText
from . import TimeFormatter

class MySolution:

    def __init__(self):
        self.startTime = None
        self.ClearTextMaxLength = 0


    def Main(self, open_FileName, thread_count, search_max_length, search_mode, use_multiThread, use_debug):
        # ハッシュ文字列が保存されたファイルの読み込み
        with open(open_FileName, mode='r') as f:
            lines = f.readlines()

        # コメント部の削除
        read_Text = ""
        for line in lines:
            line = re.sub('#.*\n', "", line)
            line = re.sub('//.*\n', "", line)
            read_Text += line

        # ハッシュアルゴリズムとハッシュ文字列の分離
        flds = read_Text.rstrip().split(':')
        algorithm = flds[0]
        target_hashed_text = flds[1].rstrip()

        # 検索する平文の最大文字列長
        ClearTextMaxLength = search_max_length

        if use_multiThread:
            print("=====================================================================================\n"
                "Date               : {0}\n"
                "algorithm          : {1}\n"
                "target Hashed Text : {2}\n"
                "Collation type     : []byte\n"
                "thread count       : {3}\n"
                "search max length  : {4}\n"
                "====================================================================================="
                .format("{0:%Y-%m-%d %H:%M:%S}".format(datetime.datetime.now()), algorithm, target_hashed_text, thread_count, search_max_length))
        else:
            print("=====================================================================================\n"
                "algorithm          : {0}\n"
                "target Hashed Text : {1}\n"
                "Use MultiThre      : {2}\n"
                "search max length  : {3}\n"
                "====================================================================================="
                .format(algorithm, target_hashed_text, use_multiThread, search_max_length))

        time0 = time.time()

        # 総当たり検索実行
        self.search(target_hashed_text, algorithm, thread_count, ClearTextMaxLength, search_mode, use_multiThread, use_debug)
        time1 = time.time()

        print("Execute time ... : {0:f} [s]\n".format(time1 - time0))
    

    def search(self, target_hashed_text, algorithm, threadMax, search_ClearText_MaxLength, search_mode, use_multiThread, use_debug):
        # 使用するスレッド数の指定チェック
        if threadMax != 1 \
        and threadMax != 2 \
        and threadMax != 4 \
        and threadMax != 8 \
        and threadMax != 16:
            return

        algorithm_upper = algorithm.replace("-", "").upper()
        if algorithm_upper =="MD5":
            Algorithm_Index = 0
        elif algorithm_upper == "SHA1":
            Algorithm_Index = 1
        elif algorithm_upper == "SHA256":
            Algorithm_Index = 2
        elif algorithm_upper == "SHA386":
            Algorithm_Index = 3
        elif algorithm_upper == "SHA512":
            Algorithm_Index = 4
        elif algorithm_upper == "RIPED160":
            Algorithm_Index = 5
        else:
            Algorithm_Index = 2  # default .. "SHA256"

        # 現在の時刻を取得
        startTime = time.time()

        # １文字から指定した文字列長まで検索する。
        for i in range(1, search_ClearText_MaxLength + 1):
            # 平文検索処理用インスタンスの生成
            searchClearText = SearchClearText(Algorithm_Index, target_hashed_text, i, threadMax, 0, use_multiThread, use_debug)

            # 文字数iでの総当たり平文検索開始時刻を保存
            # current_startTime = time.time()

            #---------------------------------------------------------------------#
            # 文字数iでの総当たり平文検索開始
            #---------------------------------------------------------------------#
            resultStr = searchClearText.Get_ClearText(threadMax)

            # 総当たり平文検索終了時刻との差を取得
            ts = time.time() - startTime

            #---------------------------------------------------------------------#
            # 文字数iでの総当たり平文検索終了
            #---------------------------------------------------------------------#
            if resultStr != None:
                print("元の文字列が見つかりました！\n"
                      "\n"
                      "結果 = {0}\n"
                      "\n"
                      "解析時間 = {1}".format(resultStr, TimeFormatter.format(ts)))
                break
            else:
                print("{0}  ... {1} 文字の組み合わせ照合終了".format(TimeFormatter.format(ts), i))

        # 平文検索処理用インスタンスを解放する。
        searchClearText = None

        if resultStr == None:
            # 見つからなかった場合
            print("見つかりませんでした。")

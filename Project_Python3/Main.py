# coding: utf-8

import os
import sys
import time

""" import local liblarys """
import MySolution


def print_msg_and_exit(myname):
        print("\nUsage: python {0} <testdata.txt> <thread_count> [search_max_length]".format(myname))
        exit(0)


def main():
    argv = sys.argv
    argc = len(argv)

    if argc < 3:
        print_msg_and_exit(argv[0])

    # ファイル指定のチェック
    open_filename = argv[1]
    if not os.path.exists(open_filename):
        print("{0} not found.".format(open_filename))
        exit(0)

    # スレッド数指定のチェック
    if not argv[2].isnumeric():
        print("{0} is not numeric.".format(argv[2]))
        exit(0)

    thread_num = int(argv[2])

    # スレッド数が定義値かどうかのチェック
    if thread_num != 1 \
    and thread_num != 2 \
    and thread_num != 4 \
    and thread_num != 8 \
    and thread_num != 16:
        print("Please select thread_num from 1, 2, 4, 8, 16")
        exit(0)

    # マルチスレッド処理／直列処理の選択
    use_multiThread = True
    if argc > 3:
        workStr = argv[3].upper()
        if workStr == "TRUE":
            use_multiThread = True
        elif workStr == "FALSE":
            use_multiThread = False
        else:
            print("\"{0}\" is Invalid.".format(argv[2]))
            print("use_mutiThread ... [TRUE | FALSE]")
            print_msg_and_exit(argv[0])

    # 検索する最大文字数も指定チェック
    if argc > 4:
        if not argv[4].isnumeric():
            print("{0} in not numeric.".format(argv[4]))
            exit()

        search_max_length = int(argv[4])
        if search_max_length < 1 or search_max_length > 255:
            print("{0} is not 0 - 255".format(search_max_length))
            exit()
    else:
        search_max_length = 256

    # デバッグ出力の有効／無効
    use_debug = False
    if argc > 5:
        workStr = argv[5].upper()
        if workStr == "TRUE":
            use_debug = True
        elif workStr == "FALSE":
            use_debug = False
        else:
            print("\"{0}\" is Invalid.".format(argv[5]))
            print("use_debug ... [TRUE | FALSE]")
            print_msg_and_exit(argv[0])

    sl = MySolution.MySolution()
    sl.Main(open_filename, thread_num, search_max_length, 0, use_multiThread, use_debug)


if __name__ == "__main__":
    main()

# coding: utf-8

import os
import sys
import time

""" import local liblarys """
import MySolution


def print_msg_and_exit(myname):
        print("Usage: python {0} <testdata.txt> <thread_count> [search_max_length]".format(myname))
        exit(0)


def main():
    argv = sys.argv
    argc = len(argv)

    if argc < 2:
        print_msg_and_exit(argv[0])

    open_filename = argv[1]
    if not os.path.exists(open_filename):
        print("{0} not found.".format(open_filename))
        exit(0)


    if not argv[2].isnumeric():
        print("{0} is not numeric.".format(argv[2]))
        exit(0)

    thread_num = int(argv[2])

    if thread_num != 1 \
    and thread_num != 2 \
    and thread_num != 4 \
    and thread_num != 8 \
    and thread_num != 16:
        print("Please select thread_num from 1, 2, 4, 8, 16")
        exit(0)

    if argc > 3:
        if not argv[3].isnumeric():
            print("{0} in not numeric.".format(argv[3]))
            exit()

        search_max_length = int(argv[3])
        if search_max_length < 1 or search_max_length > 255:
            print("{0} is not 0 - 255".format(search_max_length))
            exit()
    else:
        search_max_length = 256

    sl = MySolution.MySolution()
    sl.Main(open_filename, thread_num, search_max_length, 0)


if __name__ == "__main__":
    main()

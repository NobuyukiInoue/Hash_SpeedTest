# coding: utf-8

import hashlib
import os
import sys
import time
from mylibs.getch import getch


def hashed(algorithm, source_text):
    algorithm = algorithm.replace("-", "").upper()

    print("\n"
          "\n"
          "algorithm   = {0}\n"
          "source_text = {1}".format(algorithm, source_text.decode()))

    if algorithm == "MD5":
        result_text = hashlib.md5(source_text).hexdigest()
    elif algorithm == "SHA1":
        result_text = hashlib.sha1(source_text).hexdigest()
    elif algorithm == "SHA224":
        result_text = hashlib.sha224(source_text).hexdigest()
    elif algorithm == "SHA256":
        result_text = hashlib.sha256(source_text).hexdigest()
    elif algorithm == "SHA384":
        result_text = hashlib.sha384(source_text).hexdigest()
    elif algorithm == "SHA512":
        result_text = hashlib.sha512(source_text).hexdigest()
    else:
        result_text = hashlib.md5(source_text).hexdigest()

    return "{0}:{1}".format(algorithm, result_text)


def save_file(filename, resultStr, exist_check):
    if exist_check:
        if os.path.exists(filename):
            firstKey = ''
            while (firstKey != 'Y' and firstKey != 'N'):
                print("\n{0} is exists. overwrite? (Y/N)".format(filename), end = "")
                keyRet = ord(getch())
                firstKey = chr(keyRet).upper()

            print()
            if (firstKey == 'N'):
                print(resultStr, end = "")
                return

    with open(filename, mode='w') as fp:
        fp.write(resultStr)

    print("\n{0} has been saved.".format(filename))


def main():
    argv = sys.argv
    argc = len(argv)

    if argc < 2:
        print("Usage: python {0} <output_file> [--force]".format(argv[0]))
        exit()

    filename = argv[1]

    exist_check = True
    if argc >= 3:
        if argv[2] == "-f" or argv[2] == "-force":
            exist_check = False

    print("algorithm [SHA256]? ", end="")
    algorithm = input()

    if algorithm == "":
        algorithm = "SHA256"

    print("source text = ", end="")
    source_text = input()

    resultStr = "# {0}".format(source_text) + "\n"
    resultStr += hashed(algorithm, source_text.encode('ascii')) + "\n"

    save_file(filename, resultStr, exist_check)


if __name__ == "__main__":
    main()

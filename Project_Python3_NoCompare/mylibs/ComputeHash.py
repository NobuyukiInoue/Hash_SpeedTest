# coding: utf-8

import hashlib

class ComputeHash:

    # 関数配列の呼び出し
    def ComputeHash_Common(self, i, srcArray):
        if i == 0:
            return hashlib.md5(srcArray).digest
        elif i == 1:
            return hashlib.sha1(srcArray).digest()
        elif i == 2:
            return hashlib.sha256(srcArray).digest()
        elif i == 3:
            return hashlib.sha384(srcArray).digest()
        elif i == 4:
            return hashlib.sha512(srcArray).digest()
        else:
            return hashlib.sha256(srcArray).digest()

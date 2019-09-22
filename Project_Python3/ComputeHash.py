# coding: utf-8

import hashlib

class ComputeHash:

    # 関数配列の呼び出し
    def ComputeHash_Common(self, i, srcArray):
        if i == 0:
            return hashlib.md5(srcArray).hexdigest()
        elif i == 1:
            return hashlib.sha1(srcArray).hexdigest()
        elif i == 2:
            return hashlib.sha256(srcArray).hexdigest()
        elif i == 3:
            return hashlib.sha384(srcArray).hexdigest()
        elif i == 4:
            return hashlib.sha512(srcArray).hexdigest()
        else:
            return hashlib.sha256(srcArray).hexdigest()

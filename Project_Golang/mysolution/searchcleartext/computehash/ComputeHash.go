package computehash

import (
	"crypto/md5"
	"crypto/sha1"
	"crypto/sha256"
	"crypto/sha512"
)

// ComputeHashCommon ...
// ハッシュ関数呼び出し用 for []byte
func ComputeHashCommon(AlgorithmIndex int, srcArray []byte) []byte {
	switch AlgorithmIndex {
	case 0:
		b := md5.Sum(srcArray)
		return b[:]
	case 1:
		b := sha1.Sum(srcArray)
		return b[:]
	case 2:
		b := sha256.Sum256(srcArray)
		return b[:]
	case 3:
		b := sha512.Sum384(srcArray)
		return b[:]
	case 4:
		b := sha512.Sum512(srcArray)
		return b[:]
	}

	return nil
}

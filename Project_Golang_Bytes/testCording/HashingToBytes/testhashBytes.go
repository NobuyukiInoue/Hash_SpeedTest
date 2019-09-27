package main

import (
	"crypto/md5"
	"crypto/sha1"
	"crypto/sha256"
	"crypto/sha512"
	"fmt"
	"unsafe"
)

func main() {
	printHash("")
	printHash("0\"")
	printHash("~~~")
	printHash("aaa")
}

func printHash(targetStr string) {
	vec := *(*[]byte)(unsafe.Pointer(&targetStr))
	b := ComputeHashCommonBytes(2, vec)

	res := ""
	for i := 0; i < len(b); i++ {
		res += fmt.Sprintf("%02x", b[i])
	}

	fmt.Printf("%s --> %s\n", targetStr, res)
}

// ComputeHashCommonBytes ...
// ハッシュ関数呼び出し用 for []byte
func ComputeHashCommonBytes(AlgorithmIndex int, vec []byte) []byte {
	switch AlgorithmIndex {
	case 0:
		b := md5.Sum(vec)
		return b[:]
	case 1:
		b := sha1.Sum(vec)
		return b[:]
	case 2:
		b := sha256.Sum256(vec)
		return b[:]
	case 3:
		b := sha512.Sum384(vec)
		return b[:]
	case 4:
		b := sha512.Sum512(vec)
		return b[:]
	}

	return nil
}

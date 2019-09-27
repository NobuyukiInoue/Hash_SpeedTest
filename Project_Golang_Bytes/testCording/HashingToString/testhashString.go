package gmain

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
	fmt.Printf("%s --> %s\n", targetStr, ComputeHashCommon(2, targetStr))
}

// ComputeHashCommon ...
// ハッシュ関数呼び出し用 for string
func ComputeHashCommon(AlgorithmIndex int, srcArray string) string {
	vec := *(*[]byte)(unsafe.Pointer(&srcArray))

	switch AlgorithmIndex {
	case 0:
		return ComputeHashMD5(vec)
	case 1:
		return ComputeHashSHA1(vec)
	case 2:
		return ComputeHashSHA256(vec)
	case 3:
		return ComputeHashSHA384(vec)
	case 4:
		return ComputeHashSHA512(vec)
	}

	return ""
}

// ComputeHashCommonBytes ...
// ハッシュ関数呼び出し用 for []byte
func ComputeHashCommonBytes(AlgorithmIndex int, srcArray []byte) string {
	switch AlgorithmIndex {
	case 0:
		return ComputeHashMD5(srcArray)
	case 1:
		return ComputeHashSHA1(srcArray)
	case 2:
		return ComputeHashSHA256(srcArray)
	case 3:
		return ComputeHashSHA384(srcArray)
	case 4:
		return ComputeHashSHA512(srcArray)
	}

	return ""
}

// ComputeHashMD5 ...
func ComputeHashMD5(srcArray []byte) string {
	b := md5.Sum(srcArray)
	//	return string(b[:])

	res := ""
	for i := 0; i < len(b); i++ {
		res += fmt.Sprintf("%02x", b[i])
	}
	return res
}

// ComputeHashSHA1 ...
func ComputeHashSHA1(srcArray []byte) string {
	b := sha1.Sum(srcArray)
	//	return string(b[:])

	res := ""
	for i := 0; i < len(b); i++ {
		res += fmt.Sprintf("%02x", b[i])
	}
	return res
}

// ComputeHashSHA256 ...
func ComputeHashSHA256(srcArray []byte) string {
	b := sha256.Sum256(srcArray)
	//	return string(b[:])

	res := ""
	for i := 0; i < len(b); i++ {
		res += fmt.Sprintf("%02x", b[i])
	}
	return res
}

// ComputeHashSHA384 ...
func ComputeHashSHA384(srcArray []byte) string {
	b := sha512.Sum384(srcArray)
	//	return string(b[:])

	res := ""
	for i := 0; i < len(b); i++ {
		res += fmt.Sprintf("%02x", b[i])
	}
	return res
}

// ComputeHashSHA512 ...
func ComputeHashSHA512(srcArray []byte) string {
	b := sha512.Sum512(srcArray)
	//	return string(b[:])

	res := ""
	for i := 0; i < len(b); i++ {
		res += fmt.Sprintf("%02x", b[i])
	}
	return res
}

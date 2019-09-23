package main

import "crypto/sha256"

// "crypto/sha384"

/*
func ComputeHashCommon(AlgorithmIndex int, srcArray []byte) []byte {
	switch AlgorithmIndex {
	case 0:
		return md5.md5(srcArray)
	case 1:
		return sha1.Sum1(srcArray)
	case 2:
		return sha256.Sum256(srcArray)
	case 3:
	//   return sha384.Sum384(srcArray)
	case 4:
		return sha512.Sum512(srcArray)
	default:
		return sha256.Sum256(srcArray)
	}
}
*/
func ComputeHash_Common(AlgorithmIndex int, srcArray []byte) [32]byte {
	return sha256.Sum256(srcArray)
}

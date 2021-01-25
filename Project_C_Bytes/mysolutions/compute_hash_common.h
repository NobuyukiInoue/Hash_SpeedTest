#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <openssl/md5.h>
#include <openssl/sha.h>

unsigned char* compute_hash_common(int algorithmIndex, char *message);
unsigned char* md5(char *message);
unsigned char* sha1(char *message);
unsigned char* sha256(char *message);
unsigned char* sha384(char *message);
unsigned char* sha512(char *message);

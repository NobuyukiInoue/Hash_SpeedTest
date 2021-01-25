#include "compute_hash_common.h"

unsigned char* compute_hash_common(int algorithmIndex, char *message)
{
    switch (algorithmIndex) {
    case 0:
        return md5(message);
    case 1:
        return sha1(message);
    case 2:
        return sha256(message);
    case 3:
        return sha384(message);
    case 4:
        return sha512(message);
    default:
        return sha256(message);
    }
}

unsigned char* md5(char *message)
{
    unsigned char *digest = (unsigned char *)malloc(MD5_DIGEST_LENGTH);

    MD5_CTX sha_ctx;
    MD5_Init(&sha_ctx);                             // コンテキストを初期化
    MD5_Update(&sha_ctx, message, strlen(message)); // message を入力にする
    MD5_Final(digest, &sha_ctx);                    // digest に出力

    return digest;
}

unsigned char* sha1(char *message)
{
    unsigned char *digest = (unsigned char *)malloc(SHA_DIGEST_LENGTH);

    SHA_CTX sha_ctx;
    SHA1_Init(&sha_ctx);                             // コンテキストを初期化
    SHA1_Update(&sha_ctx, message, strlen(message)); // message を入力にする
    SHA1_Final(digest, &sha_ctx);                    // digest に出力

    return digest;
}

unsigned char* sha256(char *message)
{
    unsigned char *digest = (unsigned char *)malloc(SHA256_DIGEST_LENGTH);

    SHA256_CTX sha_ctx;
    SHA256_Init(&sha_ctx);                             // コンテキストを初期化
    SHA256_Update(&sha_ctx, message, strlen(message)); // message を入力にする
    SHA256_Final(digest, &sha_ctx);                    // digest に出力

    return digest;
}

unsigned char* sha384(char *message)
{
    printf("SHA384() is not implemented.\n");
    exit(-1);
/*
    unsigned char *digest = (unsigned char *)malloc(SHA384_DIGEST_LENGTH);

    SHA384_CTX sha_ctx;
    SHA384_Init(&sha_ctx);                             // コンテキストを初期化
    SHA384_Update(&sha_ctx, message, strlen(message)); // message を入力にする
    SHA384_Final(digest, &sha_ctx);                    // digest に出力

    return digest;
*/
}

unsigned char* sha512(char *message)
{
    unsigned char *digest = (unsigned char *)malloc(SHA512_DIGEST_LENGTH);

    SHA512_CTX sha_ctx;
    SHA512_Init(&sha_ctx);                             // コンテキストを初期化
    SHA512_Update(&sha_ctx, message, strlen(message)); // message を入力にする
    SHA512_Final(digest, &sha_ctx);                    // digest に出力

    return digest;
}

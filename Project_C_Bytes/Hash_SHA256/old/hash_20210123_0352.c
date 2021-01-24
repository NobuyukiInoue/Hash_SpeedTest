#include <stdio.h>
#include <openssl/sha.h>

int main(int argc, char *argv[]) {
    char message[65536];
    unsigned char digest[SHA256_DIGEST_LENGTH];

    while (1) {
        printf("message = ");
        int res = scanf("%s", message);
        if (res == 0)
            break;

        SHA256_CTX sha_ctx;
        SHA256_Init(&sha_ctx); // コンテキストを初期化
        SHA256_Update(&sha_ctx, message, sizeof(message)); // message を入力にする
        SHA256_Final(digest, &sha_ctx); // digest に出力

        printf("%s\n", message);
        
        for (int i = 0; i < sizeof(digest); ++i) {
            printf("%x", digest[i]);
        }
        printf("\n");
    }

    return 0;
}

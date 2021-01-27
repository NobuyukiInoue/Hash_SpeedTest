#include <stdio.h>
#include <string.h>
#include <openssl/md5.h>

int main(int argc, char *argv[]) {
    char message[256];
    unsigned char digest[MD5_DIGEST_LENGTH];

    while (1) {
        message[0] = '\0';
        printf("message = ");
        int res = scanf("%255s", message);
        if (res == 0 || message[0] == '\0')
            break;

        printf("message = %s, strlen(message) = %ld\n", message, strlen(message));

        MD5_CTX sha_ctx;
        MD5_Init(&sha_ctx); // コンテキストを初期化
        MD5_Update(&sha_ctx, message, strlen(message)); // message を入力にする
        MD5_Final(digest, &sha_ctx); // digest に出力
        
        for (int i = 0; i < sizeof(digest); ++i) {
            printf("%x", digest[i]);
        }
        printf("\n");
    }

    return 0;
}

#include <stdio.h>
#include "openssl/sha.h"

void main(void)
{
    static unsigned char buffer[65];

//    sha256("string", buffer);
    printf("%s\n", buffer);	
}

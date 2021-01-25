#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int solution_start(char *openFileName, int threadCount, int searchMaxLength, int searchMode, bool enableMultiThread, bool enableDebug);
void search(char *target_hashed_text, char *algorithm, int threadCount, int ClearTextMaxLength, int searchMode, bool enableMuiltiThread, bool enableDebug);
void remove_comment(char *targetStr);
void print_digest(unsigned char *digest);

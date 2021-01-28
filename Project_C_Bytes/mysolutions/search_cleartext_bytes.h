#include <math.h>
#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "compute_hash_common.h"

struct func_args {
    int threadNum;
    int targetLength;
};

void init_searchClearTextBytes(int alg_index, char *targetStr, int strLen, int threadMax, int mode, bool use_multiThread, bool use_debug);
void init_targetChars(int mode);
void init_chr_StartEnd();
void set_chr_StartEnd();
void display_chrStartEnd(int threadNum);
char *get_clearText(int threadMax, int target_strLength);
void *bruteforce_hashing(void *args);
bool get_NextClearText_Group_All(int threadNum, int target_strLength);
bool get_NextClearText_Group_All_level2(int threadNum, int target_strLength);
void display_clearText();
int get_index_targetChars(char val);
char charToHex(char s);
bool bytesEquals(char *arr1, char *arr2);

void disp_arrays();
void disp_chr_StartEnd();
void disp_chr();
void disp_srcStr();

void free_arrays();
void free_chr();
void free_srcStr();
void free_chr_StartEnd();
void free_targetChars();

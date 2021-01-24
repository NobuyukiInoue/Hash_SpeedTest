#include <math.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include "compute_hash_common.h"

struct thread_args {
    int threadNum;
};

void init_searchClearTextBytes(int alg_index, char *targetStr, int strLen, int threadMax, int mode, bool use_multiThread, bool use_debug);
void init_targetChars(int mode);
void free_targetChars();
void init_chr_StartEnd();
void set_chr_StartEnd();
void display_chrStartEnd();
char *get_clearText(int threadMax, int target_strLength);
void *func_thread(void *args);
void save_clearTextList();
bool get_NextClearText_Group_All(int threadNum, int target_strLength);
bool get_NextClearText_Group_All_level2(int threadNum, int target_strLength);
void display_clearText();
void free_arrays();
void free_resultStrArray();
void disp_resultStrArray();
void free_clearTextList();
void free_chr();
void free_srcStr();
int get_index_targetChars(char val);
char charToHex(char s);
bool bytesEquals(char *arr1, char *arr2);

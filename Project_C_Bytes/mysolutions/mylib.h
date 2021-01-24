#include <stdbool.h>

void ml_err_exit(char* message);
int ml_p_char_array_free(char* str_array[], int size);
void ml_replace(char* buf, const char* str1, const char* str2);
int ml_trim(char *s);
int ml_split(char* str, const char* delim, char* outlist[], int outlist_maxlength);
int ml_str_to_int_array(char* str_nums, int* nums[]);
int ml_str_to_pchar_array(char* flds, char** strs[]);
void ml_print_int_array(char* var_name, int nums[], int length);
void ml_print_pchar_array(char* var_name, char *strs[], int length);
char *ml_toUpper(char* srcStr);
char* boolToCharArray(bool val);

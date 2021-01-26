#include "mylib.h"

#define MALLOC_STR_SIZE 1024

void ml_err_exit(char* message)
{
    fprintf(stderr, "%s", message);
    exit(-1);
}

int ml_p_char_array_free(char* str_array[], int size)
{
    for (int i = size - 1; i >= 0; --i)
        free(str_array[i]);

    return size;
}

int ml_split(char *str, const char *delim, char *outlist[], int outlist_maxlength)
{
    char *pos1, *pos2;
    char *new_str, *dst;
    int cnt = 0;

    pos1 = str;
    pos2 = strstr(pos1, delim);
    while (pos2 != NULL) {
        if (cnt >= outlist_maxlength - 1) {
            ml_err_exit("ml_split() outlist_size error!\n");
        }
        if ((new_str = malloc(sizeof(char) * MALLOC_STR_SIZE)) == NULL) {
            ml_err_exit("malloc failed in ml_split().\n");
        }

        dst = new_str;

        while ( pos1 < pos2 ) {
            *dst++ = *pos1++;
        }
        *dst = '\0';

        outlist[cnt++] = new_str;
        pos1 = pos2 + strlen(delim);
        pos2 = strstr(pos1, delim);
    }

    if ((new_str = malloc(sizeof(char) * MALLOC_STR_SIZE)) == NULL) {
        ml_err_exit("malloc failed in ml_split().\n");
    }

    dst = new_str;

    while (*pos1 != '\0') {
        *dst++ = *pos1++;
    }
    *dst = '\0';
    outlist[cnt++] = new_str;

    return cnt;
}

void ml_replace(char *buf, const char *str1, const char *str2)
{
    char tmp[1024 + 1];
    char *p;

    while ((p = strstr(buf, str1)) != NULL) {
        if (*str2 != '\0') {
            *p = '\0';
            p += strlen(str1);
            strcpy(tmp, p);
            strcat(buf, str2);
            strcat(buf, tmp);
        } else {
            char *src, *dst;
            src = dst = p;

            *src = '\0';
            dst += strlen(str1);
            while (*dst != '\0') {
                *src++ = *dst++;
            }
            *src = '\0';
            p += strlen(str1);
        }
    }
}

int ml_trim(char *s)
{
    int i;
    int count = 0;

    if ( s == NULL ) { /* yes */
        return -1;
    }

    if ((i = strlen(s)) <= 0) {
        return 0;
    }

    while (i > 0 && (s[i] == '\0' || s[i] == ' ' || s[i] == '\n' || s[i] == '\r')) {
        i--;
        count++;
    }

    if (i == 0) {
        s[0] = '\0';
        return count;
    }

    s[i + 1] = '\0';

    i = 0;
    while ( s[i] != '\0' && s[i] == ' ' )
        i++;

    if (i > 0)
        strcpy(s, &s[i]);

    return i + count;
}

int ml_str_to_int_array(char* flds, int* nums[])
{
    char* str_nums[65536];
    int str_nums_length = ml_split(flds, ",", str_nums, sizeof(str_nums)/sizeof(str_nums[0]));

    *nums = (int *)malloc(sizeof(int)*str_nums_length);
    if (*nums == NULL) {
        printf("str_nums_length = %d\n", str_nums_length);
        ml_err_exit("nums[] malloc error!\n");
    }
    
    int i;
    for (i = 0; i < str_nums_length; i++) {
        (*nums)[i] = strtol(str_nums[i], NULL, 10);
    }

    return i;
}

void ml_print_int_array(char* var_name, int nums[], int length)
{
    if (length <= 0) {
        printf("%s = []", var_name);
    }
    printf("%s = [%d", var_name, nums[0]);
    for (int i = 1; i < length; i++) {
        printf(", %d", nums[i]);
    }
    printf("]\n");
}

int ml_str_to_pchar_array(char* flds, char** strs[])
{
    char* strs_temp[65536];
    int strs_temp_length = ml_split(flds, ",", strs_temp, sizeof(strs_temp)/sizeof(strs_temp[0]));

    *strs = (char **)malloc(sizeof(char *)*strs_temp_length);
    if (*strs == NULL) {
        printf("str_temp_length = %d\n", strs_temp_length);
        ml_err_exit("*strs[] malloc error!\n");
    }
    
    int i;
    char *temp;
    for (i = 0; i < strs_temp_length; i++) {
        temp = (char *)malloc(strlen(strs_temp[i]) + 1);
        if (temp == NULL) {
            printf("temp malloc error. strs_temp[%d] ... %s\n", i, strs_temp[i]);
            ml_err_exit("temp malloc error. \n");
        }
        strcpy(temp, strs_temp[i]);
        (*strs)[i] = temp;
    }

    return i;
}

void ml_print_pchar_array(char* var_name, char *strs[], int length)
{
    if (length <= 0) {
        printf("%s = []", var_name);
    }
    printf("%s = [\"%s\"", var_name, strs[0]);
    for (int i = 1; i < length; i++) {
        printf(", \"%s\"", strs[i]);
    }
    printf("]\n");
}

char *ml_toUpper(char* srcStr)
{
    char *workStr = (char *)malloc(sizeof(char)*(strlen(srcStr) + 1));
    if (workStr == NULL) {
        fprintf(stderr, "malloc() error.\n");
        return NULL;
    }

    int diff = 'a' - 'A';
    for (int i = 0; i < strlen(srcStr) + 1; i++) {
        if ('a' <= srcStr[i] && srcStr[i] <= 'z') {
            workStr[i] = srcStr[i] - diff;
        } else {
            workStr[i] = srcStr[i];
        }
    }
    return workStr;
}

// bool型から文字列に変換
char* boolToCharArray(bool val)
{
    if (val)
        return "TRUE";
    return "FALSE";
}

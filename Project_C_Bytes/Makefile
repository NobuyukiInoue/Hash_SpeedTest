COMPILER=gcc
PROJECT_NAME=main

ifeq ($(OS),Windows_NT)
	TARGET_FILE=$(PROJECT_NAME).exe
	CFLAGS=-pthread -lm
	CFLAGS_DEBUG=-g -O0 -pthread -lm
	INCLUDE=-I"C:\Program Files\OpenSSL-Win64\include"
	LIBS=-L"C:\Program Files\OpenSSL-Win64\lib"
else
UNAME=${shell uname}
ifeq ($(UNAME),Darwin)
	TARGET_FILE=$(PROJECT_NAME)_for_mac
	CFLAGS=-lcrypto -pthread -lm
	CFLAGS_DEBUG=-g -O0 -lcrypto -pthread -lm
	INCLUDE=-I/usr/local/Cellar/openssl@1.1/1.1.1g/include
	LIBS=-L/usr/local/Cellar/openssl@1.1/1.1.1g/lib
else
ifeq ($(UNAME),Linux)
	TARGET_FILE=$(PROJECT_NAME)_for_linux
	CFLAGS=-lcrypto -pthread -lm
	CFLAGS_DEBUG=-g -O0 -lcrypto -pthread -lm
else
	TARGET_FILE=$(PROJECT_NAME)
	CFLAGS=-lcrypto -pthread -lm
	CFLAGS_DEBUG=-g -O0 -lcrypto -pthread -lm
endif
endif
endif

SOURCE_FILE=main.c
MYLIBS=mysolutions/mylib.c mysolutions/solution.c mysolutions/compute_hash_common.c mysolutions/search_cleartext_bytes.c mysolutions/timeformatter.c

all:	$(TARGET_FILE)

$(TARGET_FILE):	$(SOURCE_FILE) $(MYLIBS)
	$(COMPILER) -o$(TARGET_FILE) $(INCLUDE) $(LIBS) $(SOURCE_FILE) $(MYLIBS) $(CFLAGS)

debug:	$(SOURCE_FILE) $(MYLIBS)
	$(COMPILER) -o$(TARGET_FILE)_debug $(INCLUDE) $(LIBS) $(SOURCE_FILE) $(MYLIBS) $(CFLAGS_DEBUG)

clean:
	rm $(TARGET_FILE)
	rm $(TARGET_FILE)_debug

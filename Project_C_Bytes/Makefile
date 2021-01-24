COMPILER=gcc
CFLAGS=-lcrypto
CFLAGS_DEBUG=-g -O0 -lcrypto
PROJECT_NAME=main

ifeq ($(OS),Windows_NT)
	TARGET_FILE=$(PROJECT_NAME).exe
else
UNAME=${shell uname}
ifeq ($(UNAME),Darwin)
	TARGET_FILE=$(PROJECT_NAME)_for_mac
	INCLUDE=-I/usr/local/Cellar/openssl@1.1/1.1.1g/include
	LIBS=-L/usr/local/Cellar/openssl@1.1/1.1.1g/lib
else
ifeq ($(UNAME),Linux)
	TARGET_FILE=$(PROJECT_NAME)_for_linux
#	INCLUDE=-I/usr/include/openssl
#	LIBS=-L/usr/local/Cellar/openssl@1.1/1.1.1g/lib
else
	TARGET_FILE=$(PROJECT_NAME)
endif
endif
endif

SOURCE_FILE=main.c
MYLIBS=mysolutions/mylib.c mysolutions/solution.c mysolutions/compute_hash_common.c mysolutions/search_cleartext_bytes.c mysolutions/timeformatter.c

all:	$(TARGET_FILE)

$(TARGET_FILE):	$(SOURCE_FILE) $(MYLIB1) $(MYLIB2)
	$(COMPILER) -o$(TARGET_FILE) $(INCLUDE) $(LIBS) $(SOURCE_FILE) $(MYLIBS) $(CFLAGS)

debug:	$(SOURCE_FILE) $(MYLIB1) $(MYLIB2)
	$(COMPILER) -o$(TARGET_FILE) $(INCLUDE) $(LIBS) $(SOURCE_FILE) $(MYLIBS) $(CFLAGS_DEBUG)

clean:
	rm $(TARGET_FILE)

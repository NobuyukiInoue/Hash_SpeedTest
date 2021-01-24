COMPILER=gcc
CFLAGS=-lcrypto -lm
CFLAGS_DEBUG=-g -O0 -lcrypto -lm
PROJECT_NAME=hash

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
else
	TARGET_FILE=$(PROJECT_NAME)
endif
endif
endif

SOURCE_FILE=hash.c

all:	$(TARGET_FILE)

$(TARGET_FILE):	$(SOURCE_FILE)
	$(COMPILER) -o$(TARGET_FILE) $(INCLUDE) $(LIBS) $(SOURCE_FILE) $(CFLAGS)

debug:	$(SOURCE_FILE)
	$(COMPILER) -o$(TARGET_FILE) $(INCLUDE) $(LIBS) $(SOURCE_FILE) $(CFLAGS_DEBUG)

clean:
	rm $(TARGET_FILE)

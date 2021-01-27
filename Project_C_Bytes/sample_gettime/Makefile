COMPILER=gcc
CFLAGS=
CFLAGS_DEBUG=-g -O0
PROJECT_NAME=sample_gettime

ifeq ($(OS),Windows_NT)
	TARGET_FILE=$(PROJECT_NAME).exe
else
UNAME=${shell uname}
ifeq ($(UNAME),Darwin)
	TARGET_FILE=$(PROJECT_NAME)_for_mac
else
ifeq ($(UNAME),Linux)
	TARGET_FILE=$(PROJECT_NAME)_for_linux
else
	TARGET_FILE=$(PROJECT_NAME)
endif
endif
endif

SOURCE_FILE=$(PROJECT_NAME).c

all:	$(TARGET_FILE)

$(TARGET_FILE):	$(SOURCE_FILE)
	$(COMPILER) -o$(TARGET_FILE) $(SOURCE_FILE) $(CFLAGS)

debug:	$(SOURCE_FILE)
	$(COMPILER) -o$(TARGET_FILE)_debug $(SOURCE_FILE) $(CFLAGS_DEBUG)

clean:
	rm $(TARGET_FILE)

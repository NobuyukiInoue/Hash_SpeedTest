# HashingSpeedTest

# How to Build and Run


## Project for C language

This Project need the OpenSSL Library to build.

#### Ubuntu/Debian

```
$ sudo apt-get install libssl-dev
```

### Change Directory

```
$ cd /Project_C_Bytes
```

### Build

```
$ make clean
$ make
```

### Run(C language)


#### Linux

```
$ ./main_for_linux ../testdata/data_~~~.txt 4
```

#### macOS

```
$ ./main_for_mac ../testdata/data_~~~.txt 4
```


## Project for Golang

### Change Directory

```
$ cd /Project_Golang_Bytes
```

### Run(Golang)

```
$ go run main.go ../testdata/data_~~~.txt 4
```


## Project for C Sharp (.NET Core 2.x/3.0)

### Change Directory

```
$ cd Project_CS_DOTNET3.0_Bytes_LINQ
```
or
```
$ cd Project_CS_DOTNET2.0_Bytes_LINQ
```

### Run(dotnet) [Windows/macOS/Linux]

```
> dotnet run -c Release ../testdata/data_~~~.txt 4
```

## Project for Java


### Change Directory

```
$ cd Project_Java_Bytes
```

### Build

* Windows(MinGW is required)

```
> rm *.class
> mingw32-make all
```

* macOS/Linux

```
$ make clean
$ make all
```


### Run(Java)

```
$ java Main ../testdata/data_~~~.txt
```

## Project for Python3

### Change Directory
```
$ cd Project_Python3_Bytes
```

### Run(Python3)

```
$ python Main.py ../testdata/data_~~~.txt 4
```

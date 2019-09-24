package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"

	mysolution "./mySolution"
)

func main() {
	if len(os.Args) < 3 {
		printMsgAndExit()
	}

	// ファイル指定のチェック
	if exists(os.Args[1]) == false {
		fmt.Printf(os.Args[1] + " not found.\n")
		return
	}
	openFileName := os.Args[1]

	// スレッド数指定のチェック
	threadCount, err := strconv.Atoi(os.Args[2])
	if err != nil {
		fmt.Printf(os.Args[2] + " in not numeric.\n")
		return
	}

	// スレッド数が定義値かどうかのチェック
	if threadCount != 1 &&
		threadCount != 2 &&
		threadCount != 4 &&
		threadCount != 8 &&
		threadCount != 16 {
		fmt.Printf(os.Args[1] + " Please select threadCount from 1, 2, 4, 8, 16\n")
		return
	}

	// マルチスレッド処理／直列処理の選択
	enableMultiThread := true
	if len(os.Args) > 3 {
		workStr := strings.ToUpper(os.Args[3])
		if workStr == "TRUE" {
			enableMultiThread = true
		} else if workStr == "FALSE" {
			enableMultiThread = false
		} else {
			fmt.Printf("\"" + os.Args[3] + "\" is Invalid.\n")
			fmt.Printf("use_mutiThread ... [TRUE | FALSE]\n")
			printMsgAndExit()
		}
	}

	// 検索する最大文字列長の指定チェック
	searchMaxLength := 256
	if len(os.Args) > 4 {
		searchMaxLength, err = strconv.Atoi(os.Args[4])
		if err != nil {
			fmt.Printf(os.Args[4] + " in not numeric.\n")
			return
		}
		if searchMaxLength < 1 || searchMaxLength > 255 {
			fmt.Printf(os.Args[4] + " is not 0 - 255\n")
			return
		}
	} else {
		searchMaxLength = 256
	}

	// デバッグ出力の有効／無効
	enableDebug := false
	if len(os.Args) > 5 {
		workStr := strings.ToUpper(os.Args[5])
		if workStr == "TRUE" {
			enableDebug = true
		} else if workStr == "FALSE" {
			enableDebug = false
		} else {
			fmt.Printf("\"" + os.Args[5] + "\" is Invalid.\n")
			fmt.Printf("enableDebug ... [TRUE | FALSE]\n")
			printMsgAndExit()
		}
	}

	mysolution.Start(openFileName, threadCount, searchMaxLength, 0, enableMultiThread, enableDebug)
}

func printMsgAndExit() {
	fmt.Printf("\nUsage: go run <testdata.txt> <thread_count> [enableMultiThread] [searchMaxLength] [debug]")
	os.Exit(1)
}

func exists(name string) bool {
	_, err := os.Stat(name)
	return !os.IsNotExist(err)
}

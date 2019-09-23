package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	if len(os.Args) < 2 {
		print_msg_and_exit()
	}

	// ファイル指定のチェック
	if Exists(os.Args[1]) == false {
		fmt.Printf(os.Args[1] + " not found.\n")
		return
	}
	open_FileName := os.Args[1]

	// スレッド数指定のチェック
	thread_num, err := strconv.Atoi(os.Args[2])
	if err == nil {
		fmt.Printf(os.Args[2] + " in not numeric.\n")
		return
	}

	// スレッド数が定義値かどうかのチェック
	if thread_num != 1 &&
		thread_num != 2 &&
		thread_num != 4 &&
		thread_num != 8 &&
		thread_num != 16 {
		fmt.Printf(os.Args[1] + " Please select thread_num from 1, 2, 4, 8, 16\n")
		return
	}

	// マルチスレッド処理／直列処理の選択
	use_multiThread := true
	if len(os.Args) >= 3 {
		workStr := strings.ToUpper(os.Args[2])
		if workStr == "TRUE" {
			use_multiThread = true
		} else if workStr == "FALSE" {
			use_multiThread = false
		} else {
			fmt.Printf("\"" + os.Args[2] + "\" is Invalid.\n")
			fmt.Printf("use_mutiThread ... [TRUE | FALSE]\n")
			print_msg_and_exit()
		}
	}

	// 検索する最大文字列長の指定チェック
	var search_max_length int
	if len(os.Args) >= 4 {
		search_max_length, err := strconv.Atoi(os.Args[3])
		if err == nil {
			fmt.Printf(os.Args[3] + " in not numeric.\n")
			return
		}
		if search_max_length < 1 || search_max_length > 255 {
			fmt.Printf(os.Args[3] + " is not 0 - 255\n")
			return
		}
	} else {
		search_max_length = 256
	}

	// デバッグ出力の有効／無効
	var use_debug bool
	if len(os.Args) >= 5 {
		workStr := strings.ToUpper(os.Args[4])
		if workStr == "TRUE" {
			use_debug = true
		} else if workStr == "FALSE" {
			use_debug = false
		} else {
			fmt.Printf("\"" + os.Args[2] + "\" is Invalid.\n")
			fmt.Printf("use_debug ... [TRUE | FALSE]\n")
			print_msg_and_exit()
		}
	}

	MySolutionMain(open_FileName, thread_num, search_max_length, 0, use_multiThread, use_debug)
}

func print_msg_and_exit() {
	fmt.Printf("\nUsage: go run <testdata.txt> <thread_count> [use_multiThread] [search_max_length] [debug]")
	os.Exit(1)
}

func Exists(name string) bool {
	_, err := os.Stat(name)
	return !os.IsNotExist(err)
}

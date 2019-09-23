package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"regexp"
	"strconv"
	"strings"
	"time"
)

var startTime int
var ClearTextMaxLength int

func MySolutionMain(open_FileName string, thread_count int, search_max_length int, search_mode int, use_muiltiThread bool, use_debug bool) {
	// ハッシュ文字列が保存されたファイルの読み込み
	fp, _ := os.Open(open_FileName)
	readBytes, _ := ioutil.ReadAll(fp)
	readText := string(readBytes)

	// コメント部の削除
	rep := regexp.MustCompile("#.*\n")
	readText = rep.ReplaceAllString(readText, "")
	rep = regexp.MustCompile("//.*\n")
	readText = rep.ReplaceAllString(readText, "")

	// ハッシュアルゴリズムとハッシュ文字列の分離
	temp := strings.Trim(readText, "")
	flds := strings.Split(temp, ":")

	algorithm := flds[0]
	targetHashedText := strings.Trim(flds[1], "")

	// 検索する平文の最大文字列長
	ClearTextMaxLength = search_max_length

	fmt.Printf("=====================================================================================\n")
	fmt.Printf("algorithm          : " + algorithm + "\n")
	fmt.Printf("target Hashed Text : " + targetHashedText + "\n")
	if use_muiltiThread {
		fmt.Printf("thread count       : " + strconv.Itoa(thread_count) + "\n")
	} else {
		fmt.Printf("multithread        : " + strconv.FormatBool(use_muiltiThread) + "\n")
	}
	fmt.Printf("search max length  : " + strconv.Itoa(search_max_length) + "\n")
	fmt.Printf("=====================================================================================\n")

	timeStart := time.Now()

	// 総当たり検索実行
	search(targetHashedText, algorithm, thread_count, ClearTextMaxLength, search_mode, use_muiltiThread, use_debug)

	timeEnd := time.Now()
	fmt.Printf("Execute time: %.3f [ms]\n\n", timeEnd.Sub(timeStart).Seconds()*1000)
}

//-----------------------------------------------------------------------------//
// 元の文字列を検索
//-----------------------------------------------------------------------------//
func search(target_hashed_text string, algorithm string, threadMax int, search_ClearText_MaxLength int, search_mode int, use_muiltiThread bool, use_debug bool) {
	// 使用するスレッド数の指定チェック
	if threadMax != 1 &&
		threadMax != 2 &&
		threadMax != 4 &&
		threadMax != 8 &&
		threadMax != 16 {
		return
	}

	algorithmUpper := strings.Replace(algorithm, "-", "", -1)
	algorithmUpper = strings.ToUpper(algorithmUpper)

	var Algorithm_Index int
	switch algorithmUpper {
	case "MD5":
		Algorithm_Index := 0
	case "SHA1":
		Algorithm_Index := 1
	case "SHA256":
		Algorithm_Index := 2
	case "SHA386":
		Algorithm_Index := 3
	case "SHA512":
		Algorithm_Index := 4
	case "RIPED160":
		Algorithm_Index := 5
	default:
		Algorithm_Index := 2 // default .. "SHA256"
	}

	// 現在の時刻を取得
	startTime := time.Now()
	var resultStrFin [32]byte

	// １文字から指定した文字列長まで検索する。
	for target_strLength := 1; target_strLength <= search_ClearText_MaxLength; target_strLength++ {

		// 平文検索処理用インスタンスの生成
		SearchClearText(Algorithm_Index, target_hashed_text, target_strLength, threadMax, 0, use_muiltiThread, use_debug)

		// 文字数iでの総当たり平文検索開始時刻を保存
		current_startTime := time.Now()

		//---------------------------------------------------------------------//
		// 文字数iでの総当たり平文検索開始
		//---------------------------------------------------------------------//
		resultStrFin := Get_ClearText(threadMax)

		// 総当たり平文検索終了時刻との差を取得
		//	ts := time.Now() - startTime
		ts := (time.Now()).Sub(current_startTime).Seconds()

		//---------------------------------------------------------------------//
		// 文字数iでの総当たり平文検索終了
		//---------------------------------------------------------------------//
		if len(resultStrFin) > 0 {
			fmt.Printf("元の文字列が見つかりました！\r\n" +
				"\r\n" +
				"結果 = " + byte32_to_string(resultStrFin) + "\r\n" +
				"\r\n" +
				"解析時間 = " + TsFormat(ts) + "\n")
			break
		} else {
			fmt.Printf(TsFormat(ts) + " ... " + strconv.Itoa(target_strLength) + "文字の組み合わせ照合終了\n")
		}
	}

	if byte32_to_string(resultStrFin) == "" {
		// 見つからなかった場合
		fmt.Printf("見つかりませんでした。\n")
	}
}

package mysolution

import (
	"fmt"
	"io/ioutil"
	"os"
	"regexp"
	"strconv"
	"strings"
	"time"

	"./searchcleartextString"
	"./timeformatter"
)

var startTime int

// ClearTextMaxLength ... 平文の最大文字数
var ClearTextMaxLength int

// Start ...
func Start(openFileName string, threadCount int, searchMaxLength int, searchMode int, enableMuiltiThread bool, enableDebug bool) {
	// ハッシュ文字列が保存されたファイルの読み込み
	fp, _ := os.Open(openFileName)
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
	targetHashedText := strings.Replace(flds[1], " ", "", -1)
	targetHashedText = strings.Replace(targetHashedText, "\r", "", -1)
	targetHashedText = strings.Replace(targetHashedText, "\n", "", -1)

	// 検索する平文の最大文字列長
	ClearTextMaxLength = searchMaxLength

	t := time.Now()
	const layout = "2006/01/02 15:04:05"

	fmt.Println("=====================================================================================")
	fmt.Println("Date               : " + t.Format(layout))
	fmt.Println("algorithm          : " + algorithm)
	fmt.Println("target Hashed Text : " + targetHashedText)
	if enableMuiltiThread {
		fmt.Println("thread count       : " + strconv.Itoa(threadCount))
	} else {
		fmt.Println("multithread        : " + strconv.FormatBool(enableMuiltiThread))
	}
	fmt.Println("search max length  : " + strconv.Itoa(searchMaxLength))
	fmt.Println("=====================================================================================")

	timeStart := time.Now()

	// 総当たり検索実行
	search(targetHashedText, algorithm, threadCount, ClearTextMaxLength, searchMode, enableMuiltiThread, enableDebug)

	timeEnd := time.Now()
	fmt.Printf("Execute time: %.3f [ms]\n\n", timeEnd.Sub(timeStart).Seconds()*1000)
}

//-----------------------------------------------------------------------------//
// 元の文字列を検索
//-----------------------------------------------------------------------------//
func search(targetHashedText string, algorithm string, threadMax int, searchMaxLength int, searchMode int, enableMuiltiThread bool, enableDebug bool) {
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

	var AlgorithmIndex int
	switch algorithmUpper {
	case "MD5":
		AlgorithmIndex = 0
	case "SHA1":
		AlgorithmIndex = 1
	case "SHA256":
		AlgorithmIndex = 2
	case "SHA386":
		AlgorithmIndex = 3
	case "SHA512":
		AlgorithmIndex = 4
	case "RIPED160":
		AlgorithmIndex = 5
	default:
		AlgorithmIndex = 2 // default .. "SHA256"
	}

	// １文字から指定した文字列長まで検索する。
	for targetStrLength := 1; targetStrLength <= searchMaxLength; targetStrLength++ {

		// 平文検索処理用インスタンスの生成
		searchcleartextString.InitSearchClearText(AlgorithmIndex, targetHashedText, targetStrLength, threadMax, 0, enableMuiltiThread, enableDebug)

		// 文字数iでの総当たり平文検索開始時刻を保存
		currentStartTime := time.Now()

		//---------------------------------------------------------------------//
		// 文字数iでの総当たり平文検索開始
		//---------------------------------------------------------------------//
		resultStr, resultStrLen := searchcleartextString.GetClearText(threadMax)

		// 総当たり平文検索終了時刻との差を取得
		//	ts := time.Now() - startTime
		ts := (time.Now()).Sub(currentStartTime).Seconds()

		//---------------------------------------------------------------------//
		// 文字数iでの総当たり平文検索終了
		//---------------------------------------------------------------------//
		if resultStrLen >= 0 {
			fmt.Println("元の文字列が見つかりました！\r\n" +
				"\r\n" +
				"結果 = " + resultStr + "\r\n" +
				"\r\n" +
				"解析時間 = " + timeformatter.TsFormat(ts))
			break
		} else {
			fmt.Println(timeformatter.TsFormat(ts) + " ... " + strconv.Itoa(targetStrLength) + "文字の組み合わせ照合終了")
		}
	}
}

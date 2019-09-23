package main

import (
	"fmt"
	"os"
	"strconv"
	"unsafe"
)

/// 元の文字列（平文）候補を生成し、ハッシュ文字列と比較処理を行うモジュール

/// デバッグ出力の可否
var output_clearTextList bool

/// 処理済み平文の出力用文字列（デバッグ用）
var clearTextList []byte

/// マルチスレッド処理の可否
var useMultiThread bool

/// ハッシュ処理用クラスのインスタンス
//private ComputeHash computeHash = new ComputeHash();

/// 各スレッド処理終了時結果文字列
var resultStr []string

/// 検索対象のハッシュ後文字列
var target_HashedBytes [32]byte

/// 元の文字列の候補
var srcStr [][]byte

/// 元の文字列の候補（代入前）
var chr [][]byte

/// 選択したスレッド数のインデックス番号
var selectIndex int

/// 検索対象文字のコードを格納する配列
var targetChars []byte

/// 検索範囲先頭文字
var chrStart [][]int

/// 検索範囲末尾文字
var chrEnd [][]int

/// 選択したアルゴリズムのインデックス番号
var Algorithm_Index int

func SearchClearText(alg_index int, targetStr string, strLen int, threadMax int, mode int, use_multiThread bool, use_debug bool) {
	output_clearTextList = use_debug
	useMultiThread = use_multiThread

	srcStr = make([][]byte, threadMax)
	chr = make([][]byte, threadMax)

	for i := 0; i < threadMax; i++ {
		srcStr[i] = make([]byte, strLen)
		chr[i] = make([]byte, strLen)
	}

	// 選択したアルゴリズムのインデックス番号
	Algorithm_Index = alg_index

	// 選択したスレッド数のインデックス番号の指定（配列の選択)
	switch threadMax {
	case 1:
		selectIndex = 0
	case 2:
		selectIndex = 1
	case 4:
		selectIndex = 2
	case 8:
		selectIndex = 3
	case 16:
		selectIndex = 4
	default:
		fmt.Printf("threadMax = " + strconv.Itoa(threadMax) + ", threadMax is Invalid...\n")
		os.Exit(0)
	}

	// ハッシュ後の検索対象文字列をセット
	target_HashedBytes = string_to_bytes(targetStr)

	// 検索範囲配列の初期化
	targetChars_Init(mode)
}

//-----------------------------------------------------------------------------//
// 検索対象文字を配列にセットする。
//-----------------------------------------------------------------------------//
func targetChars_Init(mode int) {
	// 検索範囲配列の初期化
	chr_StartEnd_Init()

	switch mode {
	case 0:
		// 英数文字および記号が対象のとき
		targetChars = make([]byte, 0x7f-0x20)

		i := 0
		for num := (byte)(0x20); num < 0x7f; num++ {
			targetChars[i] = num
			i++
		}
	case 1:
		// 英数文字のみが対象のとき
		targetChars = make([]byte, ('9'-'0')+1+('Z'-'A')+1+('z'-'a')+1)

		i := 0
		for num := (byte)(0x20); num < 0x7f; num++ {
			targetChars[i] = num
			i++
		}
		i = 0
		for num := (byte)('A'); num <= 'Z'; num++ {
			targetChars[i] = num
			i++
		}
		for num := (byte)('a'); num <= 'z'; num++ {
			targetChars[i] = num
			i++
		}
	}

	// 検索範囲配列の初期化
	chr_StartEnd_Set()
}

//-----------------------------------------------------------------------------//
// 検索範囲配列の初期化
//-----------------------------------------------------------------------------//
func chr_StartEnd_Init() {
	chrStart = make([][]int, 5)
	chrEnd = make([][]int, 5)

	chrStart[0] = make([]int, 1)
	chrStart[1] = make([]int, 2)
	chrStart[2] = make([]int, 4)
	chrStart[3] = make([]int, 8)
	chrStart[4] = make([]int, 16)

	chrEnd[0] = make([]int, 1)
	chrEnd[1] = make([]int, 2)
	chrEnd[2] = make([]int, 4)
	chrEnd[3] = make([]int, 8)
	chrEnd[4] = make([]int, 16)
}

//-----------------------------------------------------------------------------//
// 各スレッドごとの対象範囲のセット
//-----------------------------------------------------------------------------//
func chr_StartEnd_Set() {
	//-------------------------------------------------------------------------//
	// スレッド数が１のときの開始・終了文字
	//-------------------------------------------------------------------------//
	chrStart[0][0] = 0
	chrEnd[0][0] = len(targetChars)

	//-------------------------------------------------------------------------//
	// スレッド数が２（配列インデックス=1）のときの開始・終了文字
	//-------------------------------------------------------------------------//
	chrStart[1][0] = 0
	chrStart[1][1] = len(targetChars) / 2
	chrEnd[1][0] = chrStart[1][1]
	chrEnd[1][1] = len(targetChars)

	//-------------------------------------------------------------------------//
	// スレッド数が４（配列インデックス=2）のときの開始・終了文字
	//-------------------------------------------------------------------------//
	chrStart[2][0] = 0
	chrStart[2][1] = 1 * len(targetChars) / 4
	chrStart[2][2] = 2 * len(targetChars) / 4
	chrStart[2][3] = 3 * len(targetChars) / 4
	chrEnd[2][0] = chrStart[2][1]
	chrEnd[2][1] = chrStart[2][2]
	chrEnd[2][2] = chrStart[2][3]
	chrEnd[2][3] = len(targetChars)

	//-------------------------------------------------------------------------//
	// スレッド数が８（配列インデックス=3）のときの開始・終了文字
	//-------------------------------------------------------------------------//
	chrStart[3][0] = 0
	chrStart[3][1] = 1 * len(targetChars) / 8
	chrStart[3][2] = 2 * len(targetChars) / 8
	chrStart[3][3] = 3 * len(targetChars) / 8
	chrStart[3][4] = 4 * len(targetChars) / 8
	chrStart[3][5] = 5 * len(targetChars) / 8
	chrStart[3][6] = 6 * len(targetChars) / 8
	chrStart[3][7] = 7 * len(targetChars) / 8
	chrEnd[3][0] = chrStart[3][1]
	chrEnd[3][1] = chrStart[3][2]
	chrEnd[3][2] = chrStart[3][3]
	chrEnd[3][3] = chrStart[3][4]
	chrEnd[3][4] = chrStart[3][5]
	chrEnd[3][5] = chrStart[3][6]
	chrEnd[3][6] = chrStart[3][7]
	chrEnd[3][7] = len(targetChars)

	//-------------------------------------------------------------------------//
	// スレッド数が１６（配列インデックス=4）のときの開始・終了文字
	//-------------------------------------------------------------------------//
	chrStart[4][0] = 0
	chrStart[4][1] = 1 * len(targetChars) / 16
	chrStart[4][2] = 2 * len(targetChars) / 16
	chrStart[4][3] = 3 * len(targetChars) / 16
	chrStart[4][4] = 4 * len(targetChars) / 16
	chrStart[4][5] = 5 * len(targetChars) / 16
	chrStart[4][6] = 6 * len(targetChars) / 16
	chrStart[4][7] = 7 * len(targetChars) / 16
	chrStart[4][8] = 8 * len(targetChars) / 16
	chrStart[4][9] = 9 * len(targetChars) / 16
	chrStart[4][10] = 10 * len(targetChars) / 16
	chrStart[4][11] = 11 * len(targetChars) / 16
	chrStart[4][12] = 12 * len(targetChars) / 16
	chrStart[4][13] = 13 * len(targetChars) / 16
	chrStart[4][14] = 14 * len(targetChars) / 16
	chrStart[4][15] = 15 * len(targetChars) / 16
	chrEnd[4][0] = chrStart[4][1]
	chrEnd[4][1] = chrStart[4][2]
	chrEnd[4][2] = chrStart[4][3]
	chrEnd[4][3] = chrStart[4][4]
	chrEnd[4][4] = chrStart[4][5]
	chrEnd[4][5] = chrStart[4][6]
	chrEnd[4][6] = chrStart[4][7]
	chrEnd[4][7] = chrStart[4][8]
	chrEnd[4][8] = chrStart[4][9]
	chrEnd[4][9] = chrStart[4][10]
	chrEnd[4][10] = chrStart[4][11]
	chrEnd[4][11] = chrStart[4][12]
	chrEnd[4][12] = chrStart[4][13]
	chrEnd[4][13] = chrStart[4][14]
	chrEnd[4][14] = chrStart[4][15]
	chrEnd[4][15] = len(targetChars)
}

//-------------------------------------------------------------------//
// 開始位置、終了位置の確認
//-------------------------------------------------------------------//
func display_chrStartEnd() {
	fmt.Printf("\nlen(targetChars) = " + strconv.Itoa(len(targetChars)) + "\n")

	for rows := 0; rows < len(chrStart); rows++ {
		for th := 0; th < len(chrStart[rows]); th++ {
			fmt.Printf("chrStart[" + strconv.Itoa(rows) + "][" + strconv.Itoa(th) + "] = " + strconv.Itoa(chrStart[rows][th]) + "\n")
			fmt.Printf("chrEnd[" + strconv.Itoa(rows) + "][" + strconv.Itoa(th) + "] = " + strconv.Itoa(chrEnd[rows][th]) + "\n")
		}
	}

	for i := 0; i < len(targetChars); i++ {
		fmt.Printf("targetChars[" + strconv.Itoa(i) + "] = " + fmt.Sprintf("%02x", targetChars[i]) + "\n")
	}
}

//-------------------------------------------------------------------//
// 元の文字列総当たり検索
//-------------------------------------------------------------------//
func Get_ClearText(threadMax int) [32]byte {
	//-------------------------------------------------------------------------//
	// 平文が""かどうかを判定する。
	//-------------------------------------------------------------------------//
	if target_HashedBytes == ComputeHash_Common(Algorithm_Index, string_to_bytes("")) {
		return string_to_bytes32("")
	}

	//-------------------------------------------------------------------------//
	// 平文が１文字以上の文字列の場合
	//-------------------------------------------------------------------------//
	resultStr := make([][]byte, threadMax)

	if useMultiThread {
		//---------------------------------------------------------------------//
		// スレッド生成
		//---------------------------------------------------------------------//

	} else {
		//---------------------------------------------------------------------//
		// 直列実行
		//---------------------------------------------------------------------//
		for threadNum := 0; threadNum < threadMax; threadNum++ {
			// 指定したアルゴリズムにてハッシュ値を生成する。
			if Get_NextClearText_Group_All(threadNum, 0) {
				//-----------------------------------------------------------//
				// 同じハッシュ値が生成できる元の文字列が見つかった場合
				//-----------------------------------------------------------//
				ClearText := make([]byte, len(srcStr[threadNum]))
				for i := 0; i < len(srcStr[threadNum]); i++ {
					ClearText[i] = srcStr[threadNum][i]
				}

				resultStr[threadNum] = ClearText
			} else {
				resultStr[threadNum] = nil
			}
		}
	}
	//-------------------------------------------------------------------------//
	// 指定文字数での結果報告
	//-------------------------------------------------------------------------//
	for true {
		resultCount := 0

		for i := 0; i < threadMax; i++ {
			if resultStr[i] != nil {
				if len(resultStr[i]) > 0 {
					// デバッグ用出力
					if output_clearTextList {
						save_clearTextList()
					}

					// いずれかのスレッドが文字列を返してきた場合（見つかった場合）
					return ([32]byte)(resultStr[i])
				} else {
					resultCount++

					if resultCount >= threadMax {
						// デバッグ用出力
						if output_clearTextList {
							save_clearTextList()
						}

						// すべて""だった場合（見つからなかった場合）
						return nil
					}
				}
			}
		}
	}

	return nil
}

//-------------------------------------------------------------------//
// 検索した平文リストのファイルへの保存
//-------------------------------------------------------------------//
func save_clearTextList() {
	file, err := os.Create("ClearTextList_" + strconv.Itoa(len(srcStr[0])) + ".txt")
	if err != nil {
		// Openエラー処理
	}
	defer file.Close()

	file.Write(clearTextList)
}

//-------------------------------------------------------------------//
// 当該階層の平文候補を生成しハッシュ値と比較する。
// 見つからなければ次の階層へ。
//-------------------------------------------------------------------//
// Get_NextClearText_Group_All(threadNum, srcStr, chr, 0) {
func Get_NextClearText_Group_All(threadNum int, target_strLength int) bool {
	// 文字列の長さの上限を超えた場合は中止する。
	if target_strLength > len(chr[threadNum])-1 {
		return false
	}

	srcStr[threadNum] = chr[threadNum]

	// まずは文字列長iの候補をチェック
	for index := chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++ {
		chr[threadNum][target_strLength] = targetChars[index]
		srcStr[threadNum][target_strLength] = chr[threadNum][target_strLength]

		// デバッグ用出力
		if output_clearTextList {
			clearTextList = append(clearTextList, '"')
			for i := 0; i < len(srcStr[threadNum]); i++ {
				clearTextList = append(clearTextList, srcStr[threadNum][i])
			}
			clearTextList = append(clearTextList, '"')
			clearTextList = append(clearTextList, '\r')
			clearTextList = append(clearTextList, '\n')
		}

		// 指定したアルゴリズムにてハッシュ値を生成する。
		if target_HashedBytes == ComputeHash_Common(Algorithm_Index, srcStr[threadNum]) {
			return true
		}
	}

	// 文字列長i + 1の候補をチェック
	for index := chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++ {
		chr[threadNum][target_strLength] = targetChars[index]

		if Get_NextClearText_Group_All_level2(threadNum, target_strLength+1) {
			return true
		}
	}

	return false
}

//-------------------------------------------------------------------//
// 当該階層の平文候補を生成しハッシュ値と比較する。
// 見つからなければ次の階層へ。
//-------------------------------------------------------------------//
func Get_NextClearText_Group_All_level2(threadNum int, target_strLength int) bool {
	// 文字列の長さの上限を超えた場合は中止する。
	if target_strLength > len(chr[threadNum])-1 {
		return false
	}

	srcStr[threadNum] = make([]byte, target_strLength+1)

	for col := 0; col < target_strLength; col++ {
		srcStr[threadNum][col] = chr[threadNum][col]
	}

	// まずは文字列長iの候補をチェック
	for index := chrStart[0][0]; index < chrEnd[0][0]; index++ {
		chr[threadNum][target_strLength] = targetChars[index]
		srcStr[threadNum][target_strLength] = chr[threadNum][target_strLength]

		// デバッグ用出力
		if output_clearTextList {
			clearTextList = append(clearTextList, '"')
			for i := 0; i < len(srcStr[threadNum]); i++ {
				clearTextList = append(clearTextList, srcStr[threadNum][i])
			}
			clearTextList = append(clearTextList, '"')
			clearTextList = append(clearTextList, '\r')
			clearTextList = append(clearTextList, '\n')
		}

		// 指定したアルゴリズムにてハッシュ値を生成する。
		if target_HashedBytes == ComputeHash_Common(Algorithm_Index, srcStr[threadNum]) {
			return true
		}
	}

	// 文字列長i + 1の候補をチェック
	for index := chrStart[0][0]; index < chrEnd[0][0]; index++ {
		chr[threadNum][target_strLength] = targetChars[index]

		if Get_NextClearText_Group_All_level2(threadNum, target_strLength+1) {
			return true
		}
	}

	return false
}

//-------------------------------------------------------------------//
// 指定した文字(byte型)が、targetChars[]の何番目かを調べる
//-------------------------------------------------------------------//
func get_index_targetChars(val byte) int {
	for i := 0; i < len(targetChars); i++ {
		if val == targetChars[i] {
			return i
		}
	}
	return 0
}

//-------------------------------------------------------------------//
// []byte を string にキャストする
//-------------------------------------------------------------------//
func byte_to_string(b []byte) string {
	return *(*string)(unsafe.Pointer(&b))
}

//-------------------------------------------------------------------//
// [32]byte を string にキャストする
//-------------------------------------------------------------------//
func byte32_to_string(b [32]byte) string {
	return *(*string)(unsafe.Pointer(&b))
}

//-------------------------------------------------------------------//
// string を []byte にキャストする
//-------------------------------------------------------------------//
func string_to_bytes(s string) []byte {
	return *(*[]byte)(unsafe.Pointer(&s))
}

//-------------------------------------------------------------------//
// string を [32]byte にキャストする
//-------------------------------------------------------------------//
func string_to_bytes32(s string) [32]byte {
	return *(*[32]byte)(unsafe.Pointer(&s))
}

package main

import "fmt"

// TsFormat ... 秒を時刻フォーマット文字列に変換する
func TsFormat(sec float64) string {
	millis := sec * 1000
	day := (int)(millis / (1000 * 60 * 60 * 24))
	hour := (int)((millis / (1000 * 60 * 60))) % 24
	minute := (int)((millis / (1000 * 60))) % 60
	second := (int)((millis / 1000)) % 60
	millisSec := (int)(millis) % 1000

	if day > 0 {
		return fmt.Sprintf("%d day + %02d:%02d:%02d:%03d", day, hour, minute, second, millisSec)
	}

	return fmt.Sprintf("%02d:%02d:%02d:%03d", hour, minute, second, millisSec)
}

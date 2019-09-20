using System;
using System.IO;
//using System.Threading.Tasks;

namespace Project_CS
{
    public class Solution
    {
        private DateTime startTime;
        private int ClearTextMaxLength;

        public void Main(string open_FileName, int thread_count, int search_max_length, int search_mode)
        {
            // ハッシュ文字列が保存されたファイルの読み込み
            StreamReader sr = new StreamReader(open_FileName);
            string read_Text = sr.ReadToEnd();
            sr.Close();

            // コメント部の削除
            read_Text = System.Text.RegularExpressions.Regex.Replace(read_Text, "#.*\n", String.Empty);
            read_Text = System.Text.RegularExpressions.Regex.Replace(read_Text, "//.*\n", String.Empty);

            // ハッシュアルゴリズムとハッシュ文字列の分離
            string[] flds = read_Text.Trim().Split(':');
            string algorithm = flds[0];
            string target_hashed_text = flds[1].Trim();

            // 検索する平文の最大文字列長
            ClearTextMaxLength = search_max_length;

            Console.WriteLine("=====================================================================================");
            Console.WriteLine("algorithm          : " + algorithm);
            Console.WriteLine("target Hashed Text : " + target_hashed_text);
            Console.WriteLine("thread count       : " + thread_count.ToString());
            Console.WriteLine("search max length  : " + search_max_length.ToString());
            Console.WriteLine("=====================================================================================");

            System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
            sw.Start();

            // 総当たり検索実行
            search(target_hashed_text, algorithm, thread_count, ClearTextMaxLength, search_mode);

            sw.Stop();
            Console.WriteLine("Total Execute time ... " + sw.ElapsedMilliseconds.ToString() + " ms\n");
        }

        //-----------------------------------------------------------------------------//
        // 元の文字列を検索
        //-----------------------------------------------------------------------------//
        // private async void search(string target_hashed_text, string algorithm, int threadMax, int search_ClearText_MaxLength)
        private void search(string target_hashed_text, string algorithm, int threadMax, int search_ClearText_MaxLength, int search_mode)
        {
            // 使用するスレッド数の指定チェック
            if ((threadMax != 1)
            && (threadMax != 2)
            && (threadMax != 4)
            && (threadMax != 8)
            && (threadMax != 16))
            {
                return;
            }

            string algorithm_upper = algorithm.Replace("-", "").ToUpper();
            int Algorithm_Index;

            switch (algorithm_upper) {
            case "MD5":
                Algorithm_Index = 0;
                break;
            case "SHA1":
                Algorithm_Index = 1;
                break;
            case "SHA256":
                Algorithm_Index = 2;
                break;
            case "SHA386":
                Algorithm_Index = 3;
                break;
            case "SHA512":
                Algorithm_Index = 4;
                break;
            case "RIPED160":
                Algorithm_Index = 5;
                break;
            default:
                Algorithm_Index = 2;  // default .. "SHA256"
                break;
            }

            // 現在の時刻を取得
            startTime = DateTime.Now;

            // 結果の初期化
            string resultStr = "";
            Search_ClearText search_cleartext;

            ComputeHash ch = new ComputeHash();
            ch = null;

            // １文字から指定した文字列長まで検索する。
            for (int i = 1; i <= search_ClearText_MaxLength; i++)
            {
                // 平文検索処理用インスタンスの生成
                search_cleartext = new Search_ClearText(Algorithm_Index, target_hashed_text, i, threadMax, 0);

                // 文字数iでの総当たり平文検索開始時刻を保存
                DateTime current_startTime = DateTime.Now;

                //---------------------------------------------------------------------//
                // 文字数iでの総当たり平文検索開始
                //---------------------------------------------------------------------//
                /*
                await Task.Run(() =>
                {
                    // 文字列の検索を開始
                    resultStr = search_cleartext.Get_ClearText(threadMax);
                });
                */
                resultStr = search_cleartext.Get_ClearText(threadMax);

                // 総当たり平文検索終了時刻との差を取得
                TimeSpan ts = DateTime.Now - startTime;

                //---------------------------------------------------------------------//
                // 文字数iでの総当たり平文検索終了
                //---------------------------------------------------------------------//
                if (resultStr != null)
                {
                    System.Console.WriteLine("元の文字列が見つかりました！\r\n"
                                    + "\r\n"
                                    + "結果 = " + resultStr + "\r\n"
                                    + "\r\n"
                                    + "解析時間 = " + ts.ToString(@"hh\:mm\:ss\.fff") + " 秒");
                    break;
                }
                else
                {
                    Console.WriteLine(ts.ToString(@"hh\:mm\:ss\.fff") + " ... " + i.ToString() + "文字の組み合わせ照合終了");

                    /*
                    if (i == 2)
                    {
                        // 2桁目まで終わったら、2桁の処理時間を基に予想終了時刻を算出する。
                        TimeSpan oneLengthTime = DateTime.Now - current_startTime;
                        TimeSpan resultTime;

                        System.Console.WriteLine("OneLength_T = " + oneLengthTime.ToString(@"hh\:mm\:ss\.fff") + " 秒");

                        if (search_mode == 0)
                        {
                            resultTime = get_finTime_for_all(oneLengthTime);
                        }
                        else if (search_mode == 1)
                        {
                            resultTime = get_finTime_for_alfaNum(oneLengthTime);
                        }
                        else
                        {
                            resultTime = get_finTime_for_all(oneLengthTime);
                        }

                        System.Console.WriteLine("予想処理時間(2文字までの処理時間で算出) : " + resultTime.ToString(@"hh\:mm\:ss\.fff") + " 秒");

                        if (resultTime == new TimeSpan(0, 0, 0))
                        {
                            //System.Console.WriteLine("予想時間上限値超過");
                        }
                        else
                        {
                            if (resultTime.Days == 0)
                            {
                                System.Console.WriteLine("予想処理時間 : " + resultTime.ToString(@"hh\:mm\:ss") + " 秒");
                            }
                            else
                            {
                                System.Console.WriteLine("予想処理時間 : " + resultTime.Days + " days " + resultTime.ToString(@"hh\:mm\:ss") + " 秒");
                            }
                        }
                    }
                    */
                }
            }
            // 平文検索処理用インスタンスを解放する。
            search_cleartext = null;

            if (resultStr == null)
            {
                // 見つからなかった場合
                System.Console.WriteLine("見つかりませんでした。");
            }
        }

        //-----------------------------------------------------------------------------//
        /// 終了予測時間を算出する(英数のみ ... A-Z, a-z, 0-9)
        //-----------------------------------------------------------------------------//
        private TimeSpan get_finTime_for_alfaNum(TimeSpan dt)
        {
            TimeSpan[] resultArray = new TimeSpan[ClearTextMaxLength];

            // 各桁の予想処理時間の初期化
            resultArray[1] = dt;

            for (int len = 2; len < ClearTextMaxLength; len++)
            {
                resultArray[len] = new TimeSpan(0, 0, 0);
            }

            // 各桁の予想処理時間の算出
            for (int len = 2; len < ClearTextMaxLength; len++)
            {
                for (int i = 0; i < ('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1; i++)
                {
                    try
                    {
                        resultArray[len] += resultArray[len - 1];
                    }
                    catch
                    {
                        return (new TimeSpan(0, 0, 0));
                    }
                }
            }

            // 各桁の処理時間を合算する
            TimeSpan resultTime = new TimeSpan(0, 0, 0);

            for (int len = 2; len < ClearTextMaxLength; len++)
            {
                resultTime += resultArray[len];
            }

            return resultTime;
        }

        //-----------------------------------------------------------------------------//
        /// 終了予測時間を算出する(英数 + 記号 ... 0x20 - 0x7f)
        //-----------------------------------------------------------------------------//
        private TimeSpan get_finTime_for_all(TimeSpan dt)
        {
            TimeSpan[] resultArray = new TimeSpan[ClearTextMaxLength];

            // 各桁の予想処理時間の初期化
            resultArray[1] = dt;

            for (int len = 2; len < ClearTextMaxLength; len++)
            {
                resultArray[len] = new TimeSpan(0, 0, 0);
            }

            // 各桁の予想処理時間の算出
            for (int len = 2; len < ClearTextMaxLength; len++)
            {
                for (int i = 0x20; i < 0x7f; i++)
                {
                    try
                    {
                        resultArray[len] += resultArray[len - 1];
                    }
                    catch
                    {
                        return new TimeSpan(0, 0, 0);
                    }
                }
            }

            // 各桁の処理時間を合算する
            TimeSpan resultTime = new TimeSpan(0, 0, 0);

            for (int len = 2; len < ClearTextMaxLength; len++)
            {
                resultTime += resultArray[len];
            }

            return resultTime;
        }
    }
}

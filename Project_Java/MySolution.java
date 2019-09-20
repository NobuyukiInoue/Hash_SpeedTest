import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MySolution {
    private Date startTime;
    private int ClearTextMaxLength;

    public void Main(String open_FileName, int thread_count, int search_max_length, int search_mode) {
        // ハッシュ文字列が保存されたファイルの読み込み
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(open_FileName));
        String string = reader.readLine();
        while (string != null){
            builder.append(string + System.getProperty("line.separator"));
            string = reader.readLine();
        }
        String read_Text = builder.toString();

        // コメント部の削除
        read_Text = read_Text.replaceAll("#.*\n", "").replaceAll("//.*\n", "");

        // ハッシュアルゴリズムとハッシュ文字列の分離
        String[] flds = read_Text.trim().split(":");
        String algorithm = flds[0];
        String target_hashed_text = flds[1].trim();

        // 検索する平文の最大文字列長
        ClearTextMaxLength = search_max_length;

        System.out.println("===============================================================");
        System.out.println("algorithm          : " + algorithm);
        System.out.println("target Hashed Text : " + target_hashed_text);
        System.out.println("thread count       : " + Integer.toString(thread_count));
        System.out.println("search max length  : " + Integer.toString(search_max_length));
        System.out.println("===============================================================");

        long start = System.currentTimeMillis();

        // 総当たり検索実行
        search(target_hashed_text, algorithm, thread_count, ClearTextMaxLength, search_mode);

        long end = System.currentTimeMillis();
        System.out.println("Total Execute time ... " + (end - start)  + "ms\n");
    }

    //-----------------------------------------------------------------------------//
    // 元の文字列を検索
    //-----------------------------------------------------------------------------//
    // private async void search(String target_hashed_text, String algorithm, int threadMax, int search_ClearText_MaxLength)
    private void search(String target_hashed_text, String algorithm, int threadMax, int search_ClearText_MaxLength, int search_mode) {
        // 使用するスレッド数の指定チェック
        if ((threadMax != 1)
        && (threadMax != 2)
        && (threadMax != 4)
        && (threadMax != 8)
        && (threadMax != 16)) {
            return;
        }

        String algorithm_upper = algorithm.replace("-", "").toUpperCase();
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
        startTime = new Date();

        // 結果の初期化
        String resultStr = "";
        Search_ClearText search_cleartext;

        ComputeHash ch = new ComputeHash();
        ch = null;

        // １文字から指定した文字列長まで検索する。
        for (int i = 1; i <= search_ClearText_MaxLength; i++) {
            // 平文検索処理用インスタンスの生成
            search_cleartext = new Search_ClearText(Algorithm_Index, target_hashed_text, i, threadMax, 0);

            // 文字数iでの総当たり平文検索開始時刻を保存
            Date current_startTime = new Date();

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
        	TimeSpan ts = (new Date()) - startTime;

            //---------------------------------------------------------------------//
            // 文字数iでの総当たり平文検索終了
            //---------------------------------------------------------------------//
            if (resultStr != null) {
                /*
                System.out.println("元の文字列が見つかりました！\r\n"
                                + "\r\n"
                                + "結果 = " + resultStr + "\r\n"
                                + "\r\n"
                                + "解析時間 = " +  ts.toString(@"hh\:mm\:ss\.fff") + " 秒");
                */
                System.out.println("元の文字列が見つかりました！\r\n"
                                + "\r\n"
                                + "結果 = " + resultStr + "\r\n"
                                + "\r\n"
                                + "解析時間 = " +  TimeSpan.toString(ts) + " 秒");
                break;
            } else {
                System.out.println(TimeSpan.toString(ts) + " ... " + Integer.toString(i) + "文字の組み合わせ照合終了");

                /*
                if (i == 2)
                {
                    // 2桁目まで終わったら、2桁の処理時間を基に予想終了時刻を算出する。
                    TimeSpan oneLengthTime = Date.Now - current_startTime;
                    TimeSpan resultTime;

                    System.out.println("OneLength_T = " + oneLengthTime.toString(@"hh\:mm\:ss\.fff") + " 秒");

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

                    System.out.println("予想処理時間(2文字までの処理時間で算出) : " + resultTime.toString(@"hh\:mm\:ss\.fff") + " 秒");

                    if (resultTime == new TimeSpan(0, 0, 0))
                    {
                        //System.out.println("予想時間上限値超過");
                    }
                    else
                    {
                        if (resultTime.Days == 0)
                        {
                            System.out.println("予想処理時間 : " + resultTime.toString(@"hh\:mm\:ss") + " 秒");
                        }
                        else
                        {
                            System.out.println("予想処理時間 : " + resultTime.Days + " days " + resultTime.toString(@"hh\:mm\:ss") + " 秒");
                        }
                    }
                }
               */
            }
        }
        // 平文検索処理用インスタンスを解放する。
        search_cleartext = null;

        if (resultStr == null) {
            // 見つからなかった場合
            System.out.println("見つかりませんでした。");
        }
    }

    //-----------------------------------------------------------------------------//
    /// 終了予測時間を算出する(英数のみ ... A-Z, a-z, 0-9)
    //-----------------------------------------------------------------------------//
    private TimeSpan get_finTime_for_alfaNum(TimeSpan dt) {
        TimeSpan[] resultArray = new TimeSpan[ClearTextMaxLength];

        // 各桁の予想処理時間の初期化
        resultArray[1] = dt;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultArray[len] = new TimeSpan(0, 0, 0);
        }

        // 各桁の予想処理時間の算出
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0; i < ('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch(Exception e) {
                    return (new TimeSpan(0, 0, 0));
                } finally {
                }
            }
        }

        // 各桁の処理時間を合算する
        TimeSpan resultTime = new TimeSpan(0, 0, 0);

        for (int len = 2; len < ClearTextMaxLength; len++) {
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

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultArray[len] = new TimeSpan(0, 0, 0);
        }

        // 各桁の予想処理時間の算出
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0x20; i < 0x7f; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch(Exception e) {
                    return new TimeSpan(0, 0, 0);
                }
            }
        }

        // 各桁の処理時間を合算する
        TimeSpan resultTime = new TimeSpan(0, 0, 0);

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultTime += resultArray[len];
        }

        return resultTime;
    }
}

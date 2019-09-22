import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MySolution {
    private long startTime;
    private int ClearTextMaxLength;

    public void Main(String open_FileName, int thread_count, int search_max_length, int search_mode, boolean use_multiThread, boolean use_debug) {
        // ハッシュ文字列が保存されたファイルの読み込み
        String read_Text = read_file(open_FileName);

        // コメント部の削除
        /*
        read_Text = read_Text.replaceAll("#.*\\n", "");
        read_Text = read_Text.replaceAll("//.*\\n", "");
        */
        read_Text = read_Text.replaceAll("#.*", "");
        read_Text = read_Text.replaceAll("//.*", "");

        // ハッシュアルゴリズムとハッシュ文字列の分離
        String[] flds = read_Text.trim().split(":");
        String algorithm = flds[0];
        String target_hashed_text = flds[1].trim();

        // 検索する平文の最大文字列長
        ClearTextMaxLength = search_max_length;

        System.out.println("=====================================================================================");
        System.out.println("algorithm          : " + algorithm);
        System.out.println("target Hashed Text : " + target_hashed_text);
        if (use_multiThread) {
            System.out.println("thread count       : " + Integer.toString(thread_count));
        } else {
            System.out.println("multiThread        : " + Boolean.toString(use_multiThread));
        }
        System.out.println("search max length  : " + Integer.toString(search_max_length));
        System.out.println("=====================================================================================");

        long start = System.currentTimeMillis();

        // 総当たり検索実行
        search(target_hashed_text, algorithm, thread_count, ClearTextMaxLength, search_mode, use_multiThread, use_debug);

        long end = System.currentTimeMillis();
        System.out.println("Total Execute time ... " + (end - start)  + "ms\n");
    }

    //-----------------------------------------------------------------------------//
    // 指定ファイルの読み込み
    //-----------------------------------------------------------------------------//
    private String read_file(String open_FileName) {
        StringBuilder builder = new StringBuilder();

        try {
            File fp = new File(open_FileName);
            BufferedReader reader = new BufferedReader(new FileReader(fp));
            String string = reader.readLine();
            while (string != null){
                builder.append(string + System.getProperty("line.separator"));
                string = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(-1);
        }

        return builder.toString();
    }

    //-----------------------------------------------------------------------------//
    // 元の文字列を検索
    //-----------------------------------------------------------------------------//
    // private async void search(String target_hashed_text, String algorithm, int threadMax, int searchClearText_MaxLength)
    private void search(String target_hashed_text, String algorithm, int threadMax, int searchClearText_MaxLength, int search_mode, boolean use_multiThread, boolean use_debug) {
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
        startTime = System.currentTimeMillis();

        // 結果の初期化
        String resultStr = "";
        SearchClearText searchClearText;
//      SearchClearText_debug searchClearText;

        ComputeHash ch = new ComputeHash();
        TimeFormatter timeformatter = new TimeFormatter();
        ch = null;

        // １文字から指定した文字列長まで検索する。
        for (int target_strLen = 1; target_strLen <= searchClearText_MaxLength; target_strLen++) {

            // 平文検索処理用インスタンスの生成
            searchClearText = new SearchClearText(Algorithm_Index, target_hashed_text, target_strLen, threadMax, 0, use_multiThread, use_debug);

            // 文字数iでの総当たり平文検索開始時刻を保存
            long current_startTime = System.currentTimeMillis();

            //---------------------------------------------------------------------//
            // 文字数iでの総当たり平文検索開始
            //---------------------------------------------------------------------//
            resultStr = searchClearText.Get_ClearText(threadMax);

            // 総当たり平文検索終了時刻との差を取得
            long ts = System.currentTimeMillis() - startTime;

            //---------------------------------------------------------------------//
            // 文字数iでの総当たり平文検索終了
            //---------------------------------------------------------------------//
            if (resultStr != null) {
                System.out.println("元の文字列が見つかりました！\r\n"
                                + "\r\n"
                                + "結果 = " + resultStr + "\r\n"
                                + "\r\n"
                                + "解析時間 = " + timeformatter.format(ts) + " 秒");
                break;
            } else {
                System.out.println(timeformatter.format(ts) + " ... " + Integer.toString(target_strLen) + "文字の組み合わせ照合終了");

                /*
                if (target_strLen == 2)
                {
                    // 2桁目まで終わったら、2桁の処理時間を基に予想終了時刻を算出する。
                    long oneLengthTime = System.currentTimeMillis() - current_startTime;
                    long resultTime;

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

                    //System.out.println("予想処理時間(2文字までの処理時間で算出) : " + timefomatter.format(resultTime));
                    System.out.println("guess : " + timeformatter.format(resultTime));
                }
                */
            }
        }
        // 平文検索処理用インスタンスを解放する。
        searchClearText = null;

        if (resultStr == null) {
            // 見つからなかった場合
            System.out.println("見つかりませんでした。");
        }
    }

    //-----------------------------------------------------------------------------//
    /// 終了予測時間を算出する(英数のみ ... A-Z, a-z, 0-9)
    //-----------------------------------------------------------------------------//
    private long get_finTime_for_alfaNum(long dt) {
        long[] resultArray = new long[ClearTextMaxLength];

        for (int len = 2; len < resultArray.length; len++) {
            resultArray[len] = 0;
        }

        // 各桁の予想処理時間の初期化
        resultArray[1] = dt;

        // 各桁の予想処理時間の算出
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0; i < ('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch (Exception e) {
                    return 0;
                } finally {

                }
            }
        }

        // 各桁の処理時間を合算する
        long resultTime = 0;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultTime += resultArray[len];
        }

        return resultTime;
    }

    //-----------------------------------------------------------------------------//
    /// 終了予測時間を算出する(英数 + 記号 ... 0x20 - 0x7f)
    //-----------------------------------------------------------------------------//
    private long get_finTime_for_all(long dt)
    {
        long[] resultArray = new long[ClearTextMaxLength];

        // 各桁の予想処理時間の初期化
        resultArray[1] = dt;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultArray[len] = 0;
        }

        // 各桁の予想処理時間の算出
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0x20; i < 0x7f; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch (Exception e) {
                    return 0;
                }
            }
        }

        // 各桁の処理時間を合算する
        long resultTime = 0;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultTime += resultArray[len];
        }

        return resultTime;
    }
}

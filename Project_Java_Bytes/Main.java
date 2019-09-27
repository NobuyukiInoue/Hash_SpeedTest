import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        /*
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + Integer.toString(i) + "] = " + args[i]);
        }
        */
        if (args.length < 2) {
            print_msg_and_exit();
        }

        // ファイル指定のチェック
        if ((new File(args[0])).exists() == false) {
            System.out.println(args[0] + " not found.");
            print_msg_and_exit();
        }
        String open_FileName = args[0];

        // スレッド数指定のチェック
        ExInteger ei = new ExInteger();
 
        Out<Integer> thread_count_temp = new Out<Integer>();
        int thread_count;
        if (ei.TryParse(args[1], thread_count_temp) == false)
        {
            System.out.println(args[1] + " in not numeric.");
            print_msg_and_exit();
        }

    	thread_count = thread_count_temp.get();

        // スレッド数が定義値かどうかのチェック
        if ((thread_count != 1)
        && (thread_count != 2)
        && (thread_count != 4)
        && (thread_count != 8)
        && (thread_count != 16)) {
            System.out.println(args[1] + " Please select thread_count from 1, 2, 4, 8, 16");
            print_msg_and_exit();
        }

        // マルチスレッド処理／直列処理の選択
        boolean enableMultiThread = true;
        if (args.length > 2) {
            String workStr = args[2].toUpperCase();
            if (workStr.equals("TRUE")) {
                enableMultiThread = true;
            } else if (workStr.equals("FALSE")) {
                enableMultiThread = false;
            } else {
                System.out.println("\"" + args[2] + "\" is Invalid.");
                System.out.println("enableMutiThread ... [TRUE | FALSE]");
                print_msg_and_exit();
            }
        }

        // 検索する最大文字列長の指定チェック
        Out<Integer> search_max_length_temp = new Out<Integer>();
        int search_max_length;  

        if (args.length > 3) {
            if (ei.TryParse(args[3], search_max_length_temp) == false) {
                System.out.println(args[3] + " in not numeric.");
                print_msg_and_exit();
            }

            search_max_length = search_max_length_temp.get();
            if (search_max_length < 1 || search_max_length > 255) {
                System.out.println(args[3] + " is not 0 - 255");
                print_msg_and_exit();
            }
        } else {
            search_max_length = 256;
        }

        // デバッグ出力の有効／無効
        boolean enableDebug = false;
        if (args.length > 4) {
            String workStr = args[4].toUpperCase();
            if (workStr.equals("TRUE")) {
                enableDebug = true;
            } else if (workStr.equals("FALSE")) {
                enableDebug = false;
            } else {
                System.out.println("\"" + args[4] + "\" is Invalid.");
                System.out.println("enableDebug ... [TRUE | FALSE]");
                print_msg_and_exit();
            }
        }

        MySolution sl = new MySolution();
        sl.Main(open_FileName, thread_count, search_max_length, 0, enableMultiThread, enableDebug);
    }

    private static void print_msg_and_exit() {
        final String className = new Object(){}.getClass().getEnclosingClass().getName();
        System.out.println("\nUsage: java " + className + " <testdata.txt> <thread_count> [enableMultiThread] [search_max_length] [debug]");
        System.exit(1);
    }
}

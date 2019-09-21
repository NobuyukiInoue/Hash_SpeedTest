import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            print_msg_and_exit();
        }

        // ファイル指定のチェック
        if ((new File(args[0])).exists() == false) {
            System.out.println(args[0] + " not found.");
            return;
        }
        String open_FileName = args[0];

        // スレッド数指定のチェック
        ExInteger ei = new ExInteger();
 
        Out<Integer> thread_count_temp = new Out<Integer>();
        int thread_count;
        if (ei.TryParse(args[1], thread_count_temp) == false)
        {
            System.out.println(args[1] + " in not numeric.");
            return;
        }

    	thread_count = thread_count_temp.get();

        // スレッド数が定義値かどうかのチェック
        if ((thread_count != 1)
        && (thread_count != 2)
        && (thread_count != 4)
        && (thread_count != 8)
        && (thread_count != 16)) {
            System.out.println(args[1] + " Please select thread_count from 1, 2, 4, 8, 16");
            return;
        }

        // 検索する最大文字列長の指定チェック
        Out<Integer> search_max_length_temp = new Out<Integer>();
        int search_max_length;  

        if (args.length >= 3) {
            if (ei.TryParse(args[2], search_max_length_temp) == false) {
                System.out.println(args[2] + " in not numeric.");
                return;
            }

            search_max_length = search_max_length_temp.get();
            if (search_max_length < 1 || search_max_length > 255) {
                System.out.println(args[2] + " is not 0 - 255");
                return;
            }
        } else {
            search_max_length = 256;
        }

        MySolution sl = new MySolution();
        sl.Main(open_FileName, thread_count, search_max_length, 0);
    }

    private static void print_msg_and_exit() {
        System.out.println("Usage: dotnet run <testdata.txt> <thread_count> <search_max_length>");
        System.exit(1);
    }
}

using System;

namespace Project_CS
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length < 2)
            {
                print_msg_and_exit();
            }

            // ファイル指定のチェック
            if (System.IO.File.Exists(args[0]) == false)
            {
                Console.WriteLine(args[0] + " not found.");
                return;
            }
            string open_FileName = args[0];

            // スレッド数指定のチェック
            int thread_num;
            if (int.TryParse(args[1], out thread_num) == false)
            {
                Console.WriteLine(args[1] + " in not numeric.");
                return;
            }

            thread_num = int.Parse(args[1]);

            // スレッド数が定義値かどうかのチェック
            if ((thread_num != 1)
            && (thread_num != 2)
            && (thread_num != 4)
            && (thread_num != 8)
            && (thread_num != 16))
            {
                Console.WriteLine(args[1] + " Please select thread_num from 1, 2, 4, 8, 16");
                return;
            }

            // マルチスレッド処理／直列処理の選択
            Boolean use_multiThread = true;
            if (args.Length >= 3)
            {
                string workStr = args[2].ToUpper();
                if (workStr == "TRUE")
                {
                    use_multiThread = true;
                }
                else if (workStr == "FALSE") {
                    use_multiThread = false;
                }
                else
                {
                    Console.WriteLine("\"" + args[2] + "\" is Invalid.");
                    Console.WriteLine("use_mutiThread ... [TRUE | FALSE]");
                    print_msg_and_exit();
                }
            }

            // 検索する最大文字列長の指定チェック
            int search_max_length;

            if (args.Length >= 4)
            {
                if (int.TryParse(args[3], out search_max_length) == false)
                {
                    Console.WriteLine(args[3] + " in not numeric.");
                    return;
                }

                search_max_length = int.Parse(args[3]);

                if (search_max_length < 1 || search_max_length > 255)
                {
                    Console.WriteLine(args[3] + " is not 0 - 255");
                    return;
                }
            }
            else
            {
                search_max_length = 256;
            }

            // デバッグ出力の有効／無効
            Boolean use_debug = false;
            if (args.Length >= 5) {
                String workStr = args[4].ToUpper();
                if (workStr == "TRUE")
                {
                    use_debug = true;
                }
                else if (workStr == "FALSE")
                {
                    use_debug = false;
                }
                else
                {
                    Console.WriteLine("\"" + args[2] + "\" is Invalid.");
                    Console.WriteLine("use_debug ... [TRUE | FALSE]");
                    print_msg_and_exit();
                }
            }

            Solution sl = new Solution();
            sl.Main(open_FileName, thread_num, search_max_length, 0, use_multiThread, use_debug);
            sl = null;
        }

        private static void print_msg_and_exit()
        {
            Console.WriteLine("\nUsage: dotnet run <testdata.txt> <thread_count> [use_multiThread] [search_max_length] [debug]");
            Environment.Exit(-1);
        }
    }
}

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

            // 検索する最大文字列長の指定チェック
            int search_max_length;

            if (args.Length >= 3)
            {
                if (int.TryParse(args[2], out search_max_length) == false)
                {
                    Console.WriteLine(args[2] + " in not numeric.");
                    return;
                }

                search_max_length = int.Parse(args[2]);

                if (search_max_length < 1 || search_max_length > 255)
                {
                    Console.WriteLine(args[2] + " is not 0 - 255");
                    return;
                }
            }
            else
            {
                search_max_length = 256;
            }

            Solution sl = new Solution();
            sl.Main(open_FileName, thread_num, search_max_length, 0);
            sl = null;
        }

        private static void print_msg_and_exit()
        {
            Console.WriteLine("Usage: dotnet run <testdata.txt> <thread_num> <search_max_length>");
            Environment.Exit(-1);
        }
    }
}

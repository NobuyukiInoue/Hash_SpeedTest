using System.Security.Cryptography;

namespace Project_CS
{
    public class ComputeHashToString
    {
        /// ハッシュ文字列を生成する関数を配列化(引数Byte[]型)
        public delegate string funcDelegate(byte[] arg1);

        /// ハッシュ文字列を生成する関数を配列化(引数Byte[]型)
        public static funcDelegate[] HashFuncTables = {
            md5_ComputeHash,
            sha1_ComputeHash,
            sha256_ComputeHash,
            sha384_ComputeHash,
            sha512_ComputeHash
            /*,
            ripemd160_ComputeHash,
            */
        };

        /// 関数配列の呼び出し(引数string型)
        public string ComputeHash_Common(int i, byte[] var)
        {
            // 関数配列の宣言
            funcDelegate func = HashFuncTables[i];

            if (func != null)
            {
                return (func(var));
            }
            else
            {
                return ("");
            }
        }

        //-----------------------------------------------------------------------------//
        // MD5ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public string md5_ComputeHash(byte[] srcArray)
        {
            byte[] hashArray;

            MD5CryptoServiceProvider md5 = new MD5CryptoServiceProvider();

            hashArray = md5.ComputeHash(srcArray);

            string hashText = "";
            for (int i = 0; i < hashArray.Length; i++)
            {
                //hashText += Convert.ToString(hashArray[i], 16);   // 0f --> f のバグあり。
                hashText += hashArray[i].ToString("x2");
            }

            return (hashText);
        }

        //-----------------------------------------------------------------------------//
        // SHA1ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public string sha1_ComputeHash(byte[] srcArray)
        {
            byte[] hashArray;

            SHA1CryptoServiceProvider sha1 = new SHA1CryptoServiceProvider();

            hashArray = sha1.ComputeHash(srcArray);

            string hashText = "";
            for (int i = 0; i < hashArray.Length; i++)
            {
                //hashText += Convert.ToString(hashArray[i], 16);   // 0f --> f のバグあり。
                hashText += hashArray[i].ToString("x2");
            }

            return (hashText);
        }

        //-----------------------------------------------------------------------------//
        // SHA256ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public string sha256_ComputeHash(byte[] srcArray)
        {
            byte[] hashArray;

            SHA256CryptoServiceProvider sha256 = new SHA256CryptoServiceProvider();

            hashArray = sha256.ComputeHash(srcArray);

            string hashText = "";
            for (int i = 0; i < hashArray.Length; i++)
            {
                //hashText += Convert.ToString(hashArray[i], 16);   // 0f --> f のバグあり。
                hashText += hashArray[i].ToString("x2");
            }

            return (hashText);
        }

        //-----------------------------------------------------------------------------//
        // SHA384ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public string sha384_ComputeHash(byte[] srcArray)
        {
            byte[] hashArray;

            SHA384CryptoServiceProvider sha384 = new SHA384CryptoServiceProvider();

            hashArray = sha384.ComputeHash(srcArray);

            string hashText = "";
            for (int i = 0; i < hashArray.Length; i++)
            {
                //hashText += Convert.ToString(hashArray[i], 16);   // 0f --> f のバグあり。
                hashText += hashArray[i].ToString("x2");
            }

            return (hashText);
        }

        //-----------------------------------------------------------------------------//
        // SHA512ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public string sha512_ComputeHash(byte[] srcArray)
        {
            byte[] hashArray;

            SHA512CryptoServiceProvider sha512 = new SHA512CryptoServiceProvider();

            hashArray = sha512.ComputeHash(srcArray);

            string hashText = "";
            for (int i = 0; i < hashArray.Length; i++)
            {
                //hashText += Convert.ToString(hashArray[i], 16);   // 0f --> f のバグあり。
                hashText += hashArray[i].ToString("x2");
            }

            return (hashText);
        }

        //-----------------------------------------------------------------------------//
        // RIPEMD160ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        /*
        static public string ripemd160_ComputeHash(byte[] srcArray)
        {
            byte[] hashArray;

            RIPEMD160Managed ripemd160 = new RIPEMD160Managed();

            hashArray = ripemd160.ComputeHash(srcArray);

            string hashText = "";
            for (int i = 0; i < hashArray.Length; i++)
            {
                //hashText += Convert.ToString(hashArray[i], 16);   // 0f --> f のバグあり。
                hashText += hashArray[i].ToString("x2");
            }

            return (hashText);
        }
        */
    }
}

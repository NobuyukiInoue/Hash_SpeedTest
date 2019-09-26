using System.Security.Cryptography;

namespace Project_CS
{
    public class ComputeHash
    {

        /// ハッシュ文字列を生成する関数を配列化(引数Byte[]型)
        public delegate byte[] funcDelegate(byte[] arg1);

        /// ハッシュ文字列を生成する関数を配列化(引数Byte[]型)
        public static funcDelegate[] HashFuncTables = {
            md5_ComputeHash,
            sha1_ComputeHash,
            sha256_ComputeHash,
            sha384_ComputeHash,
            sha512_ComputeHash
            /*,
            ripemd160_ComputeHash
            */
        };

        /// 関数配列の呼び出し(引数byte[]型)
        public byte[] ComputeHash_Common(int i, byte[] var)
        {
            // 関数配列の宣言
            funcDelegate func = HashFuncTables[i];

            if (func != null)
            {
                return func(var);
            }
            else
            {
                return null;
            }
        }

        //-----------------------------------------------------------------------------//
        // MD5ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public byte[] md5_ComputeHash(byte[] srcArray)
        {
            MD5CryptoServiceProvider md5 = new MD5CryptoServiceProvider();
            return md5.ComputeHash(srcArray);
        }

        //-----------------------------------------------------------------------------//
        // SHA1ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public byte[] sha1_ComputeHash(byte[] srcArray)
        {
            SHA1CryptoServiceProvider sha1 = new SHA1CryptoServiceProvider();
            return sha1.ComputeHash(srcArray);
        }

        //-----------------------------------------------------------------------------//
        // SHA256ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public byte[] sha256_ComputeHash(byte[] srcArray)
        {
            SHA256CryptoServiceProvider sha256 = new SHA256CryptoServiceProvider();
            return sha256.ComputeHash(srcArray);
        }

        //-----------------------------------------------------------------------------//
        // SHA384ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public byte[] sha384_ComputeHash(byte[] srcArray)
        {
            SHA384CryptoServiceProvider sha384 = new SHA384CryptoServiceProvider();
            return sha384.ComputeHash(srcArray);
        }

        //-----------------------------------------------------------------------------//
        // SHA512ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        static public byte[] sha512_ComputeHash(byte[] srcArray)
        {
            SHA512CryptoServiceProvider sha512 = new SHA512CryptoServiceProvider();
            return sha512.ComputeHash(srcArray);
        }

        //-----------------------------------------------------------------------------//
        // RIPEMD160ハッシュ値生成（引数byte[]型）
        //-----------------------------------------------------------------------------//
        /*
        static public byte[] ripemd160_ComputeHash(byte[] srcArray)
        {
            RIPEMD160Managed ripemd160 = new RIPEMD160Managed();
            return ripemd160.ComputeHash(srcArray);
        }
        */
    }
}

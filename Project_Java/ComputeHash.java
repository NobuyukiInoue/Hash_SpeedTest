import java.util.*;
import java.security.MessageDigest;

public class ComputeHash {
    public ComputeHash() {
    }

    /// ハッシュ文字列を生成する関数を配列化(引数String型)
    //public delegate String funcDelegate(String arg1);

    // Java　メソッドを変数に格納したい
    // https://teratail.com/questions/75762

    // Javaいおける関数ポインタのやり方
    // https://itech-program.com/java/51

    // メソッド参照 - Qiita
    //https://qiita.com/pepepe/items/3d810e1bbed25768caa0

    // Javaの関数ポインタの最も近い代替は何ですか？
    // https://codeday.me/jp/qa/20181127/12340.html

    /// ハッシュ文字列を生成する関数を配列化(引数byte[]型)
    interface ByteFunction {
        String func(byte[] param);
    }

    //public static byte[]UnaryOperator[] HashFuncTables = {
    interface StringFunction[] HashFuncTables = {
        ComputeHash::md5_ComputeHash,
        ComputeHash::sha1_ComputeHash,
        ComputeHash::sha256_ComputeHash,
        ComputeHash::sha384_ComputeHash,
        ComputeHash::sha512_ComputeHash
    };

    /// 関数配列の呼び出し(引数byte[]型)
    public String ComputeHash_Common(int i, byte[] var) {
        // 関数配列の宣言
        funcDelegate func = HashFuncTables[i];

        if (func != null) {
            return (func(var));
        } else {
            return ("");
        }
    }

    //-----------------------------------------------------------------------------//
    // MD5ハッシュ値生成（引数byte[]型）
    //-----------------------------------------------------------------------------//
    static public String md5_ComputeHash(byte[] srcArray) {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashArray = digest.digest(srcArray);

        StringBuilder sb = new StringBuilder();
        for (byte d : hashArray) {
            sb.append(String.format("%02X", d));
        }
        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA1ハッシュ値生成（引数byte[]型）
    //-----------------------------------------------------------------------------//
    static public String sha1_ComputeHash(byte[] srcArray) {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashArray = digest.digest(srcArray);

        StringBuilder sb = new StringBuilder();
        for (byte d : hashArray) {
            sb.append(String.format("%02X", d));
        }
        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA256ハッシュ値生成（引数byte[]型）
    //-----------------------------------------------------------------------------//
    static public String sha256_ComputeHash(byte[] srcArray) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashArray = digest.digest(srcArray);

        StringBuilder sb = new StringBuilder();
        for (byte d : hashArray) {
            sb.append(String.format("%02X", d));
        }
        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA384ハッシュ値生成（引数byte[]型）
    //-----------------------------------------------------------------------------//
    static public String sha384_ComputeHash(byte[] srcArray) {
        MessageDigest digest = MessageDigest.getInstance("SHA-384");
        byte[] hashArray = digest.digest(srcArray);

        StringBuilder sb = new StringBuilder();
        for (byte d : hashArray) {
            sb.append(String.format("%02X", d));
        }
        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA512ハッシュ値生成（引数byte[]型）
    //-----------------------------------------------------------------------------//
    static public String sha512_ComputeHash(byte[] srcArray) {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hashArray = digest.digest(srcArray);

        StringBuilder sb = new StringBuilder();
        for (byte d : hashArray) {
            sb.append(String.format("%02X", d));
        }
        return sb.toString();
    }
}

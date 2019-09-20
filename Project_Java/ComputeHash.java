import java.util.*;
import java.security.MessageDigest;

public class ComputeHash {
    public ComputeHash() {
    }

    /// �n�b�V��������𐶐�����֐���z��(����String�^)
    //public delegate String funcDelegate(String arg1);

    // Java�@���\�b�h��ϐ��Ɋi�[������
    // https://teratail.com/questions/75762

    // Java��������֐��|�C���^�̂���
    // https://itech-program.com/java/51

    // ���\�b�h�Q�� - Qiita
    //https://qiita.com/pepepe/items/3d810e1bbed25768caa0

    // Java�̊֐��|�C���^�̍ł��߂���ւ͉��ł����H
    // https://codeday.me/jp/qa/20181127/12340.html

    /// �n�b�V��������𐶐�����֐���z��(����byte[]�^)
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

    /// �֐��z��̌Ăяo��(����byte[]�^)
    public String ComputeHash_Common(int i, byte[] var) {
        // �֐��z��̐錾
        funcDelegate func = HashFuncTables[i];

        if (func != null) {
            return (func(var));
        } else {
            return ("");
        }
    }

    //-----------------------------------------------------------------------------//
    // MD5�n�b�V���l�����i����byte[]�^�j
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
    // SHA1�n�b�V���l�����i����byte[]�^�j
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
    // SHA256�n�b�V���l�����i����byte[]�^�j
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
    // SHA384�n�b�V���l�����i����byte[]�^�j
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
    // SHA512�n�b�V���l�����i����byte[]�^�j
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

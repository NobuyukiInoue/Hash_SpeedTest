import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputeHashToString {

    /// �֐��z��̌Ăяo��(����byte[]�^)
    public String ComputeHash_Common(int i, byte[] srcArray) {
        switch (i) {
        case 0:
            return md5_ComputeHash(srcArray);
        case 1:
            return sha1_ComputeHash(srcArray);
        case 2:
            return sha256_ComputeHash(srcArray);
        case 3:
            return sha384_ComputeHash(srcArray);
        case 4:
            return sha512_ComputeHash(srcArray);
        default:
            return sha256_ComputeHash(srcArray);
        }
    }

    //-----------------------------------------------------------------------------//
    // MD5�n�b�V���l�����i����byte[]�^�j
    //-----------------------------------------------------------------------------//
    static public String md5_ComputeHash(byte[] srcArray) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashArray = digest.digest(srcArray);

            for (byte d : hashArray) {
                sb.append(String.format("%02x", d));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            System.exit(-1);
        }

        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA1�n�b�V���l�����i����byte[]�^�j
    //-----------------------------------------------------------------------------//
    static public String sha1_ComputeHash(byte[] srcArray) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashArray = digest.digest(srcArray);

            for (byte d : hashArray) {
                sb.append(String.format("%02x", d));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            System.exit(-1);
        }

        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA256�n�b�V���l�����i����byte[]�^�j
    //-----------------------------------------------------------------------------//
    static public String sha256_ComputeHash(byte[] srcArray) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashArray = digest.digest(srcArray);

            for (byte d : hashArray) {
                sb.append(String.format("%02x", d));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            System.exit(-1);
        }

        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA384�n�b�V���l�����i����byte[]�^�j
    //-----------------------------------------------------------------------------//
    static public String sha384_ComputeHash(byte[] srcArray) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] hashArray = digest.digest(srcArray);

            for (byte d : hashArray) {
                sb.append(String.format("%02x", d));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            System.exit(-1);
        }

        return sb.toString();
    }

    //-----------------------------------------------------------------------------//
    // SHA512�n�b�V���l�����i����byte[]�^�j
    //-----------------------------------------------------------------------------//
    static public String sha512_ComputeHash(byte[] srcArray) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashArray = digest.digest(srcArray);

            for (byte d : hashArray) {
                sb.append(String.format("%02x", d));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            System.exit(-1);
        }

        return sb.toString();
    }
}

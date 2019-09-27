import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputeHashToBytes {

    /// ä÷êîîzóÒÇÃåƒÇ—èoÇµ(à¯êîbyte[]å^)
    public byte[] ComputeHash_Common(int i, byte[] srcArray) {
        MessageDigest digest;
        switch (i) {
        case 0:
            try {    
                digest = MessageDigest.getInstance("MD5");
                return digest.digest(srcArray);
            } catch (NoSuchAlgorithmException e) {

            }
        case 1:
            try {
                digest = MessageDigest.getInstance("SHA-1");
                return digest.digest(srcArray);
            } catch (NoSuchAlgorithmException e) {

            }
        case 2:
            try {
                digest = MessageDigest.getInstance("SHA-256");
                return digest.digest(srcArray);
            } catch (NoSuchAlgorithmException e) {
                
            }
        case 3:
            try {
                digest = MessageDigest.getInstance("SHA-384");
                return digest.digest(srcArray);
            } catch (NoSuchAlgorithmException e) {
                
            }
        case 4:
            try {
                digest = MessageDigest.getInstance("SHA-512");
                return digest.digest(srcArray);
            } catch (NoSuchAlgorithmException e) {
                    
            }
        default:
            try {
                digest = MessageDigest.getInstance("SHA-256");
                return digest.digest(srcArray);
            } catch (NoSuchAlgorithmException e) {
                
            }
        }

        return null;
    }
}

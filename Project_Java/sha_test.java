import java.math.BigInteger;
import java.security.MessageDigest;
 
import org.apache.commons.codec.digest.DigestUtils;
 
 
public class Main3 {
 
    public static void main(String[] args) {
         
        String value = "HashValue‚¾‚æ";
        String sha1 = "";
         
        // Apache commons ‚Å‚Ì‚â‚è•û
        sha1 = DigestUtils.sha1Hex( value );
        System.out.println( "DigestUtils" );
        System.out.println( sha1 );
         
        // java •W€ ‚Å‚Ì‚â‚è•û
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] result = digest.digest(value.getBytes());
            sha1 = String.format("%040x", new BigInteger(1, result));
        } catch (Exception e){
            e.printStackTrace();
        }
         
        System.out.println( "MessageDigest" );
        System.out.println( sha1 );
         
    }
}

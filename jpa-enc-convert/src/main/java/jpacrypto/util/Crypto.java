package jpacrypto.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class Crypto {

    static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";
    static final String AES = "AES";


    public static byte [] initRandomKey() {
        final SecureRandom prng = new SecureRandom();
        var rval = new byte[128 / Byte.SIZE];
        prng.nextBytes(rval);
        return rval;
    }

    public static byte [] decrypt(byte[] key, byte [] cipherText) {
        try {
            var encryptCipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
            encryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES));
            return encryptCipher.doFinal(cipherText);
        } catch(Throwable ex) {
            throw new RuntimeException("error during encryption", ex);
        }
    }

    public static byte [] encrypt(byte [] key, byte [] plainText) {
        try {
            var encryptCipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
            encryptCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES));
            return encryptCipher.doFinal(plainText);
        } catch(Throwable ex) {
            throw new RuntimeException("error during encryption", ex);
        }
    }

    public static byte [] sha1( byte [] data ) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(data);
            return digest.digest();
        } catch(Throwable ex) {
            throw new RuntimeException("can't compute sha1", ex);
        }
    }
}

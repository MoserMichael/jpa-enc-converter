package jpacrypto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public interface CryptoBeanInterface {

    @Data
    @AllArgsConstructor
    static public class DataKey { 
        public byte [] encryptedKey;
        public byte [] plainTextKey;
    }
    DataKey makeDataKey();
    byte [] useDataKey(byte [] encryptedKey);
}

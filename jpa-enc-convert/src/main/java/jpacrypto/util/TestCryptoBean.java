package jpacrypto.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        value="encryption.mode",
        havingValue="test"
)
public class TestCryptoBean implements CryptoBeanInterface{
    @Value("${encryption.masterKey:}")
    private String masterKeyValue;

    @Override
    public DataKey makeDataKey() {

        var recordKey = Crypto.initRandomKey();

        var encryptedRecordkey = Crypto.encrypt(getMasterKey(), recordKey);

        return new CryptoBeanInterface.DataKey(encryptedRecordkey, recordKey);
    }

    @Override
    public byte[] useDataKey(byte[] encryptedKey) {
        return Crypto.decrypt(getMasterKey(), encryptedKey);
    }

    private byte [] getMasterKey() {
        if (masterKeyValue == null || masterKeyValue.equals("")) {
            throw new RuntimeException("no master key has been set");
        }
        return Hex.hexToBin(masterKeyValue);
    }
}

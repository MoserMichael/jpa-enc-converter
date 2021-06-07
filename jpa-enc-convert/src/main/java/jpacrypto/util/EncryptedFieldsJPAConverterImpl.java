package jpacrypto.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class EncryptedFieldsJPAConverterImpl<T>  {
    private final Logger logger = LoggerFactory.getLogger(EncryptedFieldsJPAConverterImpl.class);

    private CryptoBeanInterface cryptoBeanInterface;

    public EncryptedFieldsJPAConverterImpl(CryptoBeanInterface cryptoBeanInterface) {
        this.cryptoBeanInterface = cryptoBeanInterface;
    }

    public String convertToDatabaseColumn(T attribute) {

        logger.info("convertToDatabaseColumn - encrypting", attribute);

        var dataKey = cryptoBeanInterface.makeDataKey();
        var encryptedRecordkey = dataKey.getEncryptedKey();

        String recordRawData = "";
        try {
            recordRawData = new ObjectMapper().writeValueAsString(attribute);
        } catch(JsonProcessingException ex) {
            logger.error("can't serialize attribute", ex);
            throw new RuntimeException("Can't serialize attribute", ex);
        }
        byte [] recordRawDataBytes = recordRawData.getBytes(StandardCharsets.UTF_8);
        var encryptedRecordData = Crypto.encrypt(dataKey.getPlainTextKey(), recordRawDataBytes);

        return Hex.bin2hex(encryptedRecordkey) + ":" + Hex.bin2hex(encryptedRecordData);
    }


    public T convertToEntityAttribute(String dbData, Class<T> recordClass) {

        logger.info("convertToDatabaseColumn - decrypting", recordClass);


        int keyEnd = dbData.indexOf(':');
        if (keyEnd == -1) {
            logger.error("bad encrypted record format");
            return null;
        }

        byte [] key = Hex.hexToBin(dbData.substring(0,keyEnd));
        byte [] data = Hex.hexToBin(dbData.substring(keyEnd+1));

        byte [] recordKey = cryptoBeanInterface.useDataKey(key);
        byte [] plainRecordData = Crypto.decrypt(recordKey, data);

        try {
            return new ObjectMapper().readValue(plainRecordData, recordClass);
        } catch(Exception ex) {
            logger.error("can't serialize attribute", ex);
            throw new RuntimeException("Can't serialize attribute", ex);
        }
    }


}

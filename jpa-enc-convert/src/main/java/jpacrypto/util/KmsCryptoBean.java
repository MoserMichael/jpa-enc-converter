package jpacrypto.util;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@ConditionalOnProperty(
        value="encryption.mode",
        havingValue="kms"
)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class    KmsCryptoBean implements CryptoBeanInterface{

    private static long ONE_WEEK_MS = 7 * 24 * 3600 * 1000;
    @Value("${aws.region}")
    private String region;

    @Value("${aws.datakey.masterkeyId}")
    private String masterKeyId;

    @Value("${aws.datakey.keyCacheSize:1000}")
    private int keyKacheSize;

    private AWSKMS kmscl;
    private LoadingCache<ByteBuffer, ByteBuffer> plk;

    private final Logger logger = LoggerFactory.getLogger(KmsCryptoBean.class);

    private DataKey currentKey = null;
    private Lock lock = new ReentrantLock();
    private Date lastKeyTime = null;

    private LoadingCache<ByteBuffer, ByteBuffer>  plainDataKeyCache() {
        if (plk != null) {
            return plk;
        }

        plk = CacheBuilder.newBuilder().
                    maximumSize(keyKacheSize).
                    expireAfterWrite(30, TimeUnit.DAYS).
                    build(new CacheLoader<ByteBuffer, ByteBuffer>() {
                        public ByteBuffer load(ByteBuffer key) throws Exception {
                            DecryptRequest decRequest = new DecryptRequest();
                            decRequest.setCiphertextBlob(key);
                            DecryptResult decryptResult = kmsClient().decrypt(decRequest);
                            return decryptResult.getPlaintext();
                        }
                    });
        return plk;
    }


    // decrypt record key
    private AWSKMS kmsClient()  {
        if (kmscl != null) {
            return kmscl;
        }

        var kmsClientBuilder = AWSKMSClientBuilder.standard();

        kmsClientBuilder.setCredentials(new DefaultAWSCredentialsProviderChain());
        kmsClientBuilder.setRegion(region);
        kmscl = kmsClientBuilder.build();

        return kmscl;
    }


    @Override
    public DataKey makeDataKey() {
        DataKey key = getCurrentKey();
        if (key == null) {
            key = makeNewDataKey();
            setCurrentKey(key);
        }
        return key;
    }

    private DataKey getCurrentKey() {

        DataKey rkey = null;
        lock.lock();
        try {
            Date nowTime = new Date();
            if (this.currentKey != null && this.lastKeyTime != null) {
                if (this.lastKeyTime.getTime() < nowTime.getTime()) {
                    long timeDiffInMillis = nowTime.getTime() - this.lastKeyTime.getTime();
                    if (timeDiffInMillis < ONE_WEEK_MS) {
                        rkey = this.currentKey;
                    }
                }

            }
        } finally {
            lock.unlock();
        }
        return rkey;
    }

    private void setCurrentKey(DataKey key) {
        lock.lock();
        try {
            this.currentKey = key;
            this.lastKeyTime = new Date();
        } finally {
            lock.unlock();
        }
    }

    private DataKey makeNewDataKey() {

        try {
            var dataKeyRequest = new GenerateDataKeyRequest();
            dataKeyRequest.setKeyId(masterKeyId);
            dataKeyRequest.setKeySpec("AES_128");
            var result = kmsClient().generateDataKey(dataKeyRequest);
            var encryptedKey = result.getCiphertextBlob();
            var plainKey = result.getPlaintext();

            plainDataKeyCache().put(encryptedKey, plainKey);

            return new DataKey(encryptedKey.array(), plainKey.array());
        } catch(Throwable ex) {
            logger.error("Can't create data key", ex);
            throw new RuntimeException("can't create data key", ex);
        }
    }

    @Override
    public byte[] useDataKey(byte[] encryptedKey) {
        try {
            return plainDataKeyCache().get(ByteBuffer.wrap(encryptedKey)).array();
        } catch(Throwable ex) {
            logger.error("Can't decrypt data key", ex);
            throw new RuntimeException("can't decrypt data key", ex);
        }
    }
}

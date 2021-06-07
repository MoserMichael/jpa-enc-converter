package jpacrypto.db.entity;

import jpacrypto.util.EncryptedFieldsJPAConverterImpl;
import jpacrypto.util.CryptoBeanInterface;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

@Component
public class UserDetailsEncConverter implements AttributeConverter<UserDetails, String> {

    EncryptedFieldsJPAConverterImpl<UserDetails> userDetailsConverter;

    UserDetailsEncConverter(CryptoBeanInterface cryptoBeanInterface) {
        this.userDetailsConverter = new EncryptedFieldsJPAConverterImpl<UserDetails>(cryptoBeanInterface);
    }

    @Override
    public String convertToDatabaseColumn(UserDetails attribute) {
        return userDetailsConverter.convertToDatabaseColumn(attribute);
    }

    @Override
    public UserDetails convertToEntityAttribute(String dbData) {
        return userDetailsConverter.convertToEntityAttribute(dbData, UserDetails.class);
    }
}

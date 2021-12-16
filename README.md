# Spring JPA example with encryption of record

This is an example of [spring data jpa](https://spring.io/projects/spring-data-jpa) with encryption, by means of mapping to an [AttributeConverter](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/AttributeConverter.html) 

This example does the following:

- the instance of class [UserDetails](jpa-enc-convert/src/main/java/jpacrypto/db/entity/UserDetails.java) is encrypted and put into the field encrypted\_data of the sql table [users](jpa-enc-convert/src/main/resources/db/changelog/changes/v1_001.sql)  
- the instance of class UserDetails is accessible via the JPA entity class [User](jpa-enc-convert/src/main/java/jpacrypto/db/entity/User.java#21) 
    - the UserData instance is serialized into json by means of [jackson](https://github.com/FasterXML/jackson), the serialized data is then encrypted/decrypted.
    - Note that there is a dummy sql table definition [here](jpa-enc-convert/src/main/resources/db/changelog/changes/v1_002.sql) that contains no data, it exists for the purpose of mapping its fields to class UserDetails by means of JPA
- The encryption/decryption is done transparently by converter class [UserDetailsEncConverter.java](jpa-enc-convert/src/main/java/jpacrypto/db/entity/User.java) class, which is a spring component that also implements the [AttributeConverter](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/AttributeConverter.html) interface. The converter is called transparently by JPA when the encrypted UserDetails object is accessed.
    - The [UserDetailsEncConverter.java](jpa-enc-convert/src/main/java/jpacrypto/db/entity/UserDetailsEncConverter.java) bean uses the injected [CryptoBeanInterface](jpa-enc-convert/src/main/java/jpacrypto/util/CryptoBeanInterface.java) component, this component handles the creation of the per record data key, as well as the access to that data key.
    - The [UserDetailsEncConverter.java](jpa-enc-convert/src/main/java/jpacrypto/db/entity/UserDetailsEncConverter.java) bean uses template class [EncryptedFieldsJPAConverterImpl](jpa-enc-convert/src/main/java/jpacrypto/util/EncryptedFieldsJPAConverterImpl.java), this template implementation class serializes the object instance into json (by means of jackson), accesses the record key, and then encrypts/decrypts record data with the record key.

# Mode of operation 

- There is a per record data key, that is used for the encryption of one or more records. This data key is encrypted by a master key and stored in encrypted form together with the encrypted data 
- There are two modes of work. Each of the modes is implementing a spring component, that implements interface [CryptoBeanInterface](jpa-enc-convert/src/main/java/jpacrypto/util/CryptoBeanInterface.java) 
    - A test mode, when both the master and per record data key is generated randomly. This is implemented by the [TestCryptoBean](jpa-enc-convert/src/main/java/jpacrypto/util/TestCryptoBean.java) this bean is enabled when spring parameter ```encryption.mode``` is set to ```test```
    - A run mode, when the data key is generated by the KMS service of AWS, and the master key is implicitly handled by AWS. This is implemented by the [KmsCryptoBean](jpa-enc-convert/src/main/java/jpacrypto/util/KmsCryptoBean.java) this bean is enabled when spring parameter ```encryption.mode``` is set to ```kms``` ; Note that here there are additional spring parameters that need to be set, namely ```aws.datakey.masterkeyId``` and optionally ```aws.datakey.keyCacheSize```

# Tests

A unit test puts the test checks the test mode. Here you need to start the postgress docker instance by means of ```docker-compose up -d``` , then run the tests as part of ```gradle build```

Note that you also have a [psql.sh](psql.sh) script, this runs ```pql``` and connects it to the test db hosted by the docker instance.



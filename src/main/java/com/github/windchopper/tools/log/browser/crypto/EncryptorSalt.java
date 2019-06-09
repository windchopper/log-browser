package com.github.windchopper.tools.log.browser.crypto;

import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptorSalt {

    private static final int SALT_SIZE = 8;
    private static final int ITERATION_COUNT = 1000;

    private final byte[] salt;

    private EncryptorSalt(byte[] salt) {
        this.salt = salt;
    }

    public EncryptorSalt() {
        new SecureRandom().nextBytes(salt = new byte[SALT_SIZE]);
    }

    PBEParameterSpec passwordBasedEncryptionParameters() {
        return new PBEParameterSpec(salt, ITERATION_COUNT);
    }

    private String base64EncodedString() {
        return Base64.getEncoder().encodeToString(salt);
    }

    public static class XmlJavaTypeAdapter extends XmlAdapter<String, EncryptorSalt> {

        @Override public String marshal(EncryptorSalt salt) {
            return salt.base64EncodedString();
        }

        @Override public EncryptorSalt unmarshal(String base64EncodedSalt) {
            return new EncryptorSalt(Base64.getDecoder().decode(base64EncodedSalt));
        }

    }

}

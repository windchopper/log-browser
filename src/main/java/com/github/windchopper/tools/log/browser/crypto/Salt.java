package com.github.windchopper.tools.log.browser.crypto;

import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.security.SecureRandom;
import java.util.Base64;

public class Salt {

    private static final int SALT_SIZE = 8;
    private static final int ITERATION_COUNT = 1000;

    private final byte[] salt;

    private Salt(byte[] salt) {
        this.salt = salt;
    }

    public Salt() {
        new SecureRandom().nextBytes(salt = new byte[SALT_SIZE]);
    }

    PBEParameterSpec passwordBasedEncryptionParameters() {
        return new PBEParameterSpec(salt, ITERATION_COUNT);
    }

    private String base64EncodedString() {
        return Base64.getEncoder().encodeToString(salt);
    }

    public static class XmlJavaTypeAdapter extends XmlAdapter<String, Salt> {

        @Override public String marshal(Salt salt) {
            return salt == null ? null : salt.base64EncodedString();
        }

        @Override public Salt unmarshal(String base64EncodedSalt) {
            return base64EncodedSalt == null ? null : new Salt(Base64.getDecoder().decode(base64EncodedSalt));
        }

    }

}

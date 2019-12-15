package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.preferences.PreferencesEntry;
import com.github.windchopper.tools.log.browser.preferences.ByteArrayType;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@ApplicationScoped @Named("Xcryptor") public class Xcryptor {

    private static final String TRANSFORMATION = "PBEWithMD5AndTripleDES";
    private static final int SALT_SIZE = 8;
    private static final int ITERATION_COUNT = 1000;

    private final PreferencesEntry<byte[]> saltEntry = new PreferencesEntry<>(Globals.preferencesStorage, "salt", new ByteArrayType());

    private Cipher cipher;
    private PBEParameterSpec parameters;
    private SecretKey key;

    @PostConstruct void afterConstruction() {
        try {
            byte[] salt = saltEntry.load();

            if (salt == null) {
                new SecureRandom().nextBytes(salt = new byte[SALT_SIZE]);
            }

            cipher = Cipher.getInstance(TRANSFORMATION);
            parameters = new PBEParameterSpec(salt, ITERATION_COUNT);
            key = SecretKeyFactory.getInstance(cipher.getAlgorithm()).generateSecret(
                new PBEKeySpec(Optional.ofNullable(System.getProperty("user.name"))
                    .orElseThrow(() -> new IllegalStateException("Cannot determine user name"))
                    .toCharArray()));
        } catch (GeneralSecurityException thrown) {
            throw new IllegalStateException(thrown);
        }
    }

    public String encrypt(String string) throws GeneralSecurityException {
        cipher.init(Cipher.ENCRYPT_MODE, key, parameters);
        return Base64.getEncoder().encodeToString(cipher.doFinal(string.getBytes()));
    }

    public String decrypt(String string) throws GeneralSecurityException {
        cipher.init(Cipher.DECRYPT_MODE, key, parameters);
        return new String(cipher.doFinal(Base64.getDecoder().decode(string)));
    }

}

package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.preferences.PreferencesEntry
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

@ApplicationScoped @Named("Xcryptor") class Xcryptor {

    companion object {

        const val TRANSFORMATION = "PBEWithMD5AndTripleDES"
        const val SALT_SIZE = 8
        const val ITERATION_COUNT = 1000

        val saltEntry: PreferencesEntry<ByteArray> = PreferencesEntry(Globals.preferencesStorage, "salt", ByteArrayType())

    }

    private lateinit var cipher: Cipher
    private lateinit var parameters: PBEParameterSpec
    private lateinit var key: SecretKey

    @PostConstruct fun afterConstruction() {
        cipher = Cipher.getInstance(TRANSFORMATION)
        parameters = PBEParameterSpec(saltEntry.load()?:ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }, ITERATION_COUNT)
        key = SecretKeyFactory.getInstance(cipher.algorithm).generateSecret(
            PBEKeySpec(System.getProperty("user.name", "unknown").toCharArray()))
    }

    @Throws(GeneralSecurityException::class) fun encrypt(string: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, key, parameters)
        return Base64.getEncoder().encodeToString(cipher.doFinal(string.toByteArray()))
    }

    @Throws(GeneralSecurityException::class) fun decrypt(string: String?): String {
        cipher.init(Cipher.DECRYPT_MODE, key, parameters)
        return String(cipher.doFinal(Base64.getDecoder().decode(string)))
    }

}
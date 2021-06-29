package io.github.rakutentech.signatureverifier.verification

import com.nhaarman.mockitokotlin2.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import io.github.rakutentech.signatureverifier.RobolectricBaseSpec
import io.github.rakutentech.signatureverifier.SignatureVerifier
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

open class AesEncryptorSpec : RobolectricBaseSpec() {

    internal val mockKeyStore = Mockito.mock(KeyStore::class.java)
    private val mockKeyGenerator = Mockito.mock(AesKeyGenerator::class.java)
    private val mockKeyEntry = Mockito.mock(KeyStore.SecretKeyEntry::class.java)

    @Before
    open fun setup() {
        val key = generateAesKey()
        SignatureVerifier.callback = null
        When calling mockKeyGenerator.generateKey() itReturns key
        When calling mockKeyEntry.secretKey itReturns key
        When calling mockKeyStore.getEntry(any(), anyOrNull()) itReturns mockKeyEntry
    }

    @Test
    fun `it should encrypt the data`() {
        val encryptor = createAesEncryptor()

        encryptor.encrypt("test data").shouldNotBeNull()
        encryptor.encrypt("test data")?.shouldNotContain("test data")
    }

    @Test
    fun `it should attach IV`() {
        val encryptor = createAesEncryptor()

        encryptor.encrypt("test data").shouldNotBeNull()
        encryptor.encrypt("test data")?.shouldContain("\"iv\":")
    }

    @Test
    fun `it should decrypt the data`() {
        val encryptor = createAesEncryptor()

        val encryptedData = encryptor.encrypt("test data")

        encryptedData.shouldNotBeNull()
        encryptor.decrypt(encryptedData) shouldBeEqualTo "test data"
    }

    @Test
    fun `it should generate a new key when one is not in the key store`() {
        When calling mockKeyStore.getEntry(any(), anyOrNull()) itReturns null
        val encryptor = createAesEncryptor()

        encryptor.encrypt("test data")

        Mockito.verify(mockKeyGenerator).generateKey()
    }

    @Test
    fun `it should not generate a key when one is already in the key store`() {
        When calling mockKeyGenerator.generateKey() itReturns mock()
        val encryptor = createAesEncryptor()

        encryptor.encrypt("test data")

        Mockito.verify(mockKeyGenerator, never()).generateKey()
    }

    @Test
    fun `it should generate a new key when the one in the store is not an AES key`() {
        val mockRsaKeyEntry: KeyStore.PrivateKeyEntry = mock()
        When calling mockKeyStore.getEntry(any(), anyOrNull()) itReturns mockRsaKeyEntry
        val encryptor = createAesEncryptor()
        encryptor.encrypt("test data")

        Mockito.verify(mockKeyGenerator).generateKey()
    }

    internal fun createAesEncryptor(
        keyStore: KeyStore? = mockKeyStore,
        keyGenerator: AesKeyGenerator = mockKeyGenerator
    ) = AesEncryptor(keyStore, keyGenerator)

    private fun generateAesKey(): SecretKey {
        val kgen = KeyGenerator.getInstance("AES")
        kgen.init(256)
        return kgen.generateKey()
    }
}

class AesEncryptorExceptionSpec : AesEncryptorSpec() {

    private val function: (ex: Exception) -> Unit = {}
    private val mockCallback = Mockito.mock(function.javaClass)

    @Before
    override fun setup() {
        super.setup()
        SignatureVerifier.callback = mockCallback
    }

    @Test
    fun `should not crash and return valid when keystore is null`() {
        val encryptor = createAesEncryptor(keyStore = null)

        verifyFunction(encryptor, true)
    }

    @Test
    fun `should not crash and return valid when keystore throws ClassCastException`() {
        When calling mockKeyStore.getEntry(any(), anyOrNull()) itThrows ClassCastException()

        val encryptor = createAesEncryptor()

        verifyFunction(encryptor, true)
        Mockito.verify(mockCallback, times(2)).invoke(any(ClassCastException::class))
    }

    @Test
    fun `should not crash and return null when key generator throws IllegalArgumentException`() {
        When calling mockKeyStore.getEntry(any(), anyOrNull()) itReturns null

        val generator = AesKeyGenerator("", "AndroidKeyStore")

        val encryptor = createAesEncryptor(keyGenerator = generator)

        verifyFunction(encryptor, false)
        // twice called: encrypt and decrypt
        Mockito.verify(mockCallback, times(2)).invoke(any(IllegalArgumentException::class))
    }

    @Test
    fun `should not crash and return valid when keystore load throw NoSuchAlgorithmException`() {
        When calling mockKeyStore.load(isNull()) itThrows NoSuchAlgorithmException()
        val encryptor = createAesEncryptor()

        verifyFunction(encryptor, true)
        Mockito.verify(mockCallback).invoke(any(NoSuchAlgorithmException::class))
    }

    @Test
    fun `should not crash and return null when cipher throws InvalidKeyException`() {
        val mockCipher = Mockito.mock(Cipher::class.java)
        When calling mockCipher.init(eq(Cipher.DECRYPT_MODE), any(SecretKey::class), any(
            GCMParameterSpec::class)) itThrows InvalidKeyException()
        When calling mockCipher.init(eq(Cipher.ENCRYPT_MODE),
            any(SecretKey::class)) itThrows InvalidKeyException()
        val encryptor = createAesEncryptor()

        encryptor.encrypt("test data", mockCipher).shouldBeNull()
        encryptor.decrypt("test data", mockCipher).shouldBeNull()
        Mockito.verify(mockCallback, times(2)).invoke(any(InvalidKeyException::class))
    }

    private fun verifyFunction(encryptor: AesEncryptor, isValid: Boolean) {
        if (isValid) {
            val data = encryptor.encrypt("test data")
            data.shouldNotBeNull()
            data.shouldNotContain("test data")
            encryptor.decrypt(data).shouldNotBeNull()
        } else {
            encryptor.encrypt("test data").shouldBeNull()
            encryptor.decrypt("test data").shouldBeNull()
        }
    }
}

package io.github.rakutentech.signatureverifier.verification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.never
import io.github.rakutentech.signatureverifier.RobolectricBaseSpec
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher
import org.amshove.kluent.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.annotation.Config

class PublicKeyCacheSpec : RobolectricBaseSpec() {

    private val mockFetcher = Mockito.mock(PublicKeyFetcher::class.java)
    private val mockEncryptor = Mockito.mock(SharedPreferenceEncryptor::class.java)

    @Before
    fun setup() {
        When calling mockFetcher.fetch("test_key_id") itReturns "test_public_key"
        When calling mockEncryptor.getEncryptedKey(any()) itReturns "test_public_key"
    }

    @Test
    fun `should return cached key`() {
        val cache = createCache()

        cache["test_key_id"] shouldBeEqualTo "test_public_key"
        Mockito.verify(mockFetcher, never()).fetch(eq("test_key_id"))
    }

    @Test
    fun `should call fetcher fo a key id that is not cached`() {
        val cache = createCache()
        When calling mockEncryptor.getEncryptedKey(any()) itReturns null

        cache["test_key_id"] shouldBeEqualTo "test_public_key"
        Mockito.verify(mockFetcher).fetch(eq("test_key_id"))
    }

    @Test
    fun `should cache the public key between App launches`() {
        val cache = createCache()
        When calling mockEncryptor.getEncryptedKey(any()) itReturns null

        // fetched and cached
        cache["test_key_id"] shouldBeEqualTo "test_public_key"
        Mockito.verify(mockFetcher).fetch(eq("test_key_id"))

        When calling mockEncryptor.getEncryptedKey(any()) itReturns "test_public_key"

        val secondCache = createCache()

        secondCache["test_key_id"] shouldBeEqualTo "test_public_key"
        Mockito.verify(mockFetcher).fetch(eq("test_key_id"))
    }

    @Test
    fun `should set sharepreference value to null key is removed`() {
        val cache = createCache()

        cache.remove("test_key_id")
        Mockito.verify(mockEncryptor).putEncryptedKey(eq("test_key_id"), isNull())
    }

    @Test
    @Config(sdk = [22])
    fun `should return cached key when using real encryptor`() {
        TestKeyStore.setup
        val cache = createCache(encryptor = null)

        cache["test_key_id"] shouldBeEqualTo "test_public_key"
        Mockito.verify(mockFetcher).fetch(eq("test_key_id"))
    }

    @Test
    fun `should return null when using real encryptor with keystore validation issue`() {
        val cache = createCache(encryptor = null)

        cache["test_key_id"].shouldBeNull()
        Mockito.verify(mockFetcher, never()).fetch(eq("test_key_id"))
    }

    private fun createCache(
        fetcher: PublicKeyFetcher = mockFetcher,
        context: Context = ApplicationProvider.getApplicationContext(),
        encryptor: SharedPreferenceEncryptor? = mockEncryptor
    ) = PublicKeyCache(fetcher, context, encryptor)
}

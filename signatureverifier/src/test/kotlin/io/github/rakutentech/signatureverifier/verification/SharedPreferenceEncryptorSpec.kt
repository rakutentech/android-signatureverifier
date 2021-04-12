package io.github.rakutentech.signatureverifier.verification

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.isNull
import io.github.rakutentech.signatureverifier.RobolectricBaseSpec
import org.amshove.kluent.When
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.annotation.Config

class SharedPreferenceEncryptorSpec : RobolectricBaseSpec() {

    private val mockGenerator = Mockito.mock(PreferenceEncryptorKeyGenerator::class.java)
    private val mockPref = Mockito.mock(EncryptedSharedPreferences::class.java)
    private val mockEdit = Mockito.mock(SharedPreferences.Editor::class.java)

    @Before
    fun setup() {
        When calling mockGenerator.generateKey(any()) itReturns Mockito.mock(MasterKey::class.java)
        When calling mockPref.edit() itReturns mockEdit
        When calling mockEdit.putString(any(), any()) itReturns mockEdit
    }

    @Test
    fun `should put key to mock encrypted shared pref`() {
        val encryptor = createEncryptor()

        encryptor.putEncryptedKey(KEY_ID, PUBLIC_KEY)
        Mockito.verify(mockPref).edit()
        Mockito.verify(mockEdit).putString(eq(KEY_ID), eq(PUBLIC_KEY))
        Mockito.verify(mockEdit).apply()
    }

    @Test
    @Config(sdk = [22])
    fun `should put key to real encrypted shared pref`() {
        val encryptor = createEncryptor(pref = null)

        encryptor.putEncryptedKey(KEY_ID, PUBLIC_KEY)

        encryptor.getEncryptedKey(KEY_ID) shouldBeEqualTo PUBLIC_KEY
    }

    @Test
    fun `should decrypt the data`() {
        val encryptor = createEncryptor()

        When calling mockPref.getString(eq(KEY_ID), isNull()) itReturns PUBLIC_KEY

        encryptor.getEncryptedKey(KEY_ID) shouldBeEqualTo PUBLIC_KEY

        Mockito.verify(mockPref).getString(eq(KEY_ID), isNull())
    }

    @Test
    fun `should generate key once`() {
        createEncryptor(generator = mockGenerator, pref = null)

        Mockito.verify(mockGenerator).generateKey(any())
    }

    private fun createEncryptor(
        context: Context = ApplicationProvider.getApplicationContext(),
        generator: PreferenceEncryptorKeyGenerator = PreferenceEncryptorKeyGenerator("_androidx_security_master_key_"),
        pref: EncryptedSharedPreferences? = mockPref
    ) = SharedPreferenceEncryptor(context, generator, pref)

    companion object {
        private const val PUBLIC_KEY = "test_public_key"
        private const val KEY_ID = "test_key_id"

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            TestKeyStore.setup
        }
    }
}

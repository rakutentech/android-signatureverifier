package io.github.rakutentech.signatureverifier

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.any
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.InputStream

class SignatureVerifierSpec : RobolectricBaseSpec() {

    @Test
    fun `should initialize instance with RealSignatureVerifier`() {
        SignatureVerifier.init(ApplicationProvider.getApplicationContext()).shouldBeTrue()

        SignatureVerifier.instance() shouldBeInstanceOf RealSignatureVerifier::class
    }

    @Test
    fun `should initialize instance with RealSignatureVerifier with callback`() {
        SignatureVerifier.init(ApplicationProvider.getApplicationContext()) {
            Assert.fail()
        }.shouldBeTrue()

        SignatureVerifier.instance() shouldBeInstanceOf RealSignatureVerifier::class
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should instance of NotInitializedSignatureVerifier when not initialized`() = runBlockingTest {
        SignatureVerifier.setUninitializedInstance()
        SignatureVerifier.instance() shouldBeInstanceOf NotInitializedSignatureVerifier::class
        SignatureVerifier.instance().verify("any",
            Mockito.mock(InputStream::class.java), "signature").shouldBeFalse()
    }

    @Test
    fun `should return false initialization failed`() {
        val mockContext = Mockito.mock(Context::class.java)
        SignatureVerifier.init(mockContext).shouldBeFalse()
    }

    @Test
    fun `should return false initialization failed with callback`() {
        val mockContext = Mockito.mock(Context::class.java)
        val function: (ex: Exception) -> Unit = {}
        val mockCallback = Mockito.mock(function.javaClass)
        SignatureVerifier.init(mockContext, mockCallback).shouldBeFalse()

        Mockito.verify(mockCallback).invoke(any())
    }
}

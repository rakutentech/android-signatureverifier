package io.github.rakutentech.signatureverifier

import io.github.rakutentech.signatureverifier.verification.PublicKeyCache
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test
import org.mockito.Mockito
import java.io.InputStream

class SignatureVerifierSpec {

    @Test
    fun `should initialize instance with RealSignatureVerifier`() {
        SignatureVerifier.init(Mockito.mock(PublicKeyCache::class.java))

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
}

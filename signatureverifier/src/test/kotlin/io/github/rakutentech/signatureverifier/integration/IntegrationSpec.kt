package io.github.rakutentech.signatureverifier.integration

import androidx.test.core.app.ApplicationProvider
import com.ibm.icu.impl.Assert
import io.github.rakutentech.signatureverifier.IntegrationTests
import io.github.rakutentech.signatureverifier.RobolectricBaseSpec
import io.github.rakutentech.signatureverifier.SignatureVerifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(IntegrationTests::class)
class IntegrationSpec : RobolectricBaseSpec() {

    @ExperimentalCoroutinesApi
    @Test
    fun `should return true for test data`() = runBlockingTest {
        val instance = SignatureVerifier.init(
            ApplicationProvider.getApplicationContext(),
            System.getenv("IT_ENDPOINT") ?: "",
            System.getenv("IT_SUB_KEY") ?: ""
        ) {
            Assert.fail(it)
        }

        val keyId = System.getenv("IT_KEY_ID") ?: ""
        val data = System.getenv("IT_DATA") ?: ""
        val signature = System.getenv("IT_SIGNATURE") ?: ""

        instance.shouldNotBeNull()
        instance.verify(keyId, data.byteInputStream(), signature).shouldBeTrue()
    }
}

package io.github.rakutentech.signatureverifier.integration

import androidx.test.core.app.ApplicationProvider
import io.github.rakutentech.signatureverifier.IntegrationTests
import io.github.rakutentech.signatureverifier.RealSignatureVerifier
import io.github.rakutentech.signatureverifier.RobolectricBaseSpec
import io.github.rakutentech.signatureverifier.api.ApiClient
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher
import io.github.rakutentech.signatureverifier.verification.PublicKeyCache
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeTrue
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(IntegrationTests::class)
class IntegrationSpec : RobolectricBaseSpec() {

    @ExperimentalCoroutinesApi
    @Test
    fun `should return true for test data`() = runBlockingTest {
        val baseUrl = System.getenv("IT_ENDPOINT") ?: ""
        val client = ApiClient(
            baseUrl = baseUrl,
            subscriptionKey = System.getenv("IT_SUB_KEY") ?: "",
            context = ApplicationProvider.getApplicationContext()
        )

        val keyId = System.getenv("IT_KEY_ID") ?: ""
        val data = System.getenv("IT_DATA") ?: ""
        val signature = System.getenv("IT_SIGNATURE") ?: ""

        val instance = RealSignatureVerifier(
            PublicKeyCache(
                keyFetcher = PublicKeyFetcher(client),
                context = ApplicationProvider.getApplicationContext(),
                baseUrl = baseUrl
            ), TestCoroutineDispatcher()
        )
        instance.verify(keyId, data.byteInputStream(), signature).shouldBeTrue()
    }
}

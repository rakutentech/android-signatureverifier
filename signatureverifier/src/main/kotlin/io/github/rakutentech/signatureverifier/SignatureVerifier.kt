package io.github.rakutentech.signatureverifier

import android.content.Context
import androidx.annotation.VisibleForTesting
import io.github.rakutentech.signatureverifier.api.ApiClient
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher
import io.github.rakutentech.signatureverifier.verification.PublicKeyCache
import java.io.InputStream

/**
 * Main entry point for the Signature Verifier SDK.
 * Should be accessed via [SignatureVerifier.instance].
 */
@Suppress("UnnecessaryAbstractClass")
abstract class SignatureVerifier {

    /**
     * Verifies the [signature] of the [data] using the [publicKeyId].
     *
     * @return true if [signature] associated with [data] is valid.
     */
    abstract suspend fun verify(publicKeyId: String, data: InputStream, signature: String): Boolean

    companion object {
        private var instance: SignatureVerifier = NotInitializedSignatureVerifier()

        /**
         * Instance of [SignatureVerifier].
         *
         * @return [SignatureVerifier] instance
         */
        @JvmStatic
        fun instance(): SignatureVerifier = instance

        /**
         * Initializes the Signature Verifiers SDK. [errorCallback] is an optional callback function
         * for app to receive the exception that caused failed init.
         *
         * @return `true` if initialization is successful, and `false` otherwise.
         */
        fun init(context: Context, errorCallback: ((ex: Exception) -> Unit)? = null): Boolean {

            return try {
                val manifestConfig = AppManifestConfig(context)

                val client = ApiClient(
                    baseUrl = manifestConfig.baseUrl(),
                    subscriptionKey = manifestConfig.subscriptionKey(),
                    context = context
                )

                instance = RealSignatureVerifier(
                    PublicKeyCache(
                        keyFetcher = PublicKeyFetcher(client),
                        context = context
                    )
                )
                true
            } catch (ex: Exception) {
                // reset instance
                setUninitializedInstance()
                errorCallback?.let {
                    it(SignatureVerifierException("Signature Verifier initialization failed", ex))
                }
                false
            }
        }

        @VisibleForTesting
        internal fun setUninitializedInstance() {
            instance = NotInitializedSignatureVerifier()
        }
    }
}

internal class NotInitializedSignatureVerifier : SignatureVerifier() {
    override suspend fun verify(publicKeyId: String, data: InputStream, signature: String) = false
}

class SignatureVerifierException(name: String, cause: Throwable? = null) :
    RuntimeException(name, cause)

package io.github.rakutentech.signatureverifier

import androidx.annotation.VisibleForTesting
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

        internal fun init(cache: PublicKeyCache) {
            instance = RealSignatureVerifier(cache)
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

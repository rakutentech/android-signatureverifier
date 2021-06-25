package io.github.rakutentech.signatureverifier.verification

import android.content.Context
import android.util.Log
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher
import java.io.IOException

internal class PublicKeyCache(
    private val keyFetcher: PublicKeyFetcher,
    context: Context,
    prefEncryptor: SharedPreferenceEncryptor? = null
) {

    private val encryptor: SharedPreferenceEncryptor? by lazy {
        prefEncryptor ?: try {
            SharedPreferenceEncryptor(context)
        } catch (ex: SignatureVerifierException) {
            Log.d("SignatureVerifier", ex.message, ex.cause)
            null
        }
    }

    operator fun get(keyId: String): String? {
        if (encryptor == null) {
            return null
        }

        return try {
            val id = encryptor?.getEncryptedKey(keyId) ?: keyFetcher.fetch(keyId)
            encryptor?.putEncryptedKey(keyId, id)
            id
        } catch (e: IOException) {
            Log.e(TAG, "Failed to fetch public key", e)
            null
        }
    }

    fun remove(keyId: String) {
        encryptor?.putEncryptedKey(keyId, null)
    }

    companion object {
        private const val TAG = "RSV_PublicKeyCache"
    }
}

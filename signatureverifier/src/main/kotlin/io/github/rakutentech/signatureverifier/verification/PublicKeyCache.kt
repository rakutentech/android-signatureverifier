package io.github.rakutentech.signatureverifier.verification

import android.content.Context
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher

internal class PublicKeyCache(
    private val keyFetcher: PublicKeyFetcher,
    context: Context,
    prefEncryptor: SharedPreferenceEncryptor? = null
) {

    private val encryptor: SharedPreferenceEncryptor by lazy { prefEncryptor ?: SharedPreferenceEncryptor(context) }

    operator fun get(keyId: String): String {
        val key = encryptor.getEncryptedKey(keyId) ?: keyFetcher.fetch(keyId)

        encryptor.putEncryptedKey(keyId, key)

        return key
    }

    fun remove(keyId: String) {
        encryptor.putEncryptedKey(keyId, null)
    }
}

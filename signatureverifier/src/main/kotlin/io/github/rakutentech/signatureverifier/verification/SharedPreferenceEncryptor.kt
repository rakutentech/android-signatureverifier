package io.github.rakutentech.signatureverifier.verification

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

internal class SharedPreferenceEncryptor(
    context: Context,
    pref: EncryptedSharedPreferences? = null
) {

    private val sharedPref = pref ?: initEncryptedSharedPref(context)

    fun putEncryptedKey(keyId: String, key: String?) {
        sharedPref.edit().putString(keyId, key).apply()
    }

    fun getEncryptedKey(keyId: String) = sharedPref.getString(keyId, null)

    @SuppressWarnings("TooGenericExceptionCaught", "SwallowedException")
    private fun initEncryptedSharedPref(context: Context) = try {
        EncryptedSharedPreferences.create(
            context,
            "signature_verifier_prefs",
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        throw SignatureVerifierException(e.message)
    }

    companion object {
        private const val KEYSTORE_ALIAS = "signature-verifier-public-key-encryption-decryption"
    }
}

internal class SignatureVerifierException(message: String?) :
    Exception("Signature Verifier SDK cannot proceed due to keystore validation: $message")

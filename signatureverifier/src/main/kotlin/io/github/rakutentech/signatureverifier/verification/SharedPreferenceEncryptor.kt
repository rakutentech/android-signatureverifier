package io.github.rakutentech.signatureverifier.verification

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

internal class SharedPreferenceEncryptor(
    context: Context,
    keyGenerator: PreferenceEncryptorKeyGenerator = PreferenceEncryptorKeyGenerator(KEYSTORE_ALIAS),
    pref: EncryptedSharedPreferences? = null
) {

    private val sharedPref = pref ?: EncryptedSharedPreferences.create(
        context,
        "signature_verifier_prefs",
        keyGenerator.generateKey(context),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun putEncryptedKey(keyId: String, key: String?) {
        sharedPref.edit().putString(keyId, key).apply()
    }

    fun getEncryptedKey(keyId: String) = sharedPref.getString(keyId, null)

    companion object {
        private const val KEYSTORE_ALIAS = "signature-verifier-public-key-encryption-decryption"
    }
}

internal class PreferenceEncryptorKeyGenerator(private val alias: String) {
    fun generateKey(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build()
            MasterKey.Builder(context).setKeyGenParameterSpec(spec).build()
        } else {
            MasterKey.Builder(context).build()
        }
}

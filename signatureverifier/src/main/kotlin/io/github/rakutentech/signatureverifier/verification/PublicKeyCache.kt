package io.github.rakutentech.signatureverifier.verification

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import io.github.rakutentech.signatureverifier.SignatureVerifier
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher
import java.io.File

internal class PublicKeyCache(
    private val keyFetcher: PublicKeyFetcher,
    context: Context,
    baseUrl: String,
    encryptor: AesEncryptor? = null
) {

    private val encryptor: AesEncryptor by lazy { encryptor ?: AesEncryptor() }

    private val file: File by lazy {
        // replace all non-alphanumeric characters to '.':
        // - multiple/group of non-alphanumeric will be replace by one '.'
        // - trailing period is removed
        val filepath = baseUrl.replace(Regex("[^a-zA-Z0-9]+"), ".").replace(Regex(".$"), "")
        File(
            context.noBackupFilesDir,
            ("signature.keys.$filepath").substring(0, MAX_PATH)
        )
    }

    private val keys: MutableMap<String, String> by lazy {
        if (file.exists()) {
            val text = file.readText()

            if (text.isNotBlank()) {
                Gson().fromJson(text, keys.javaClass)
            } else {
                mutableMapOf()
            }
        } else {
            mutableMapOf()
        }
    }

    operator fun get(keyId: String): String {
        val encryptedKey = keys[keyId]

        return if (encryptedKey != null) {
            encryptor.decrypt(encryptedKey) ?: fetch(keyId)
        } else {
            fetch(keyId)
        }
    }

    fun remove(keyId: String) {
        keys.remove(keyId)

        file.writeText(Gson().toJson(keys))
    }

    private fun fetch(keyId: String): String {
        val key = keyFetcher.fetch(keyId)
        encryptor.encrypt(key)?.let {
            keys[keyId] = it
            file.writeText(Gson().toJson(keys))
        }

        return key
    }

    companion object {
        private const val MAX_PATH = 100
    }
}

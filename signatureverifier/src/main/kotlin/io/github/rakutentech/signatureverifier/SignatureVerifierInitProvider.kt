package io.github.rakutentech.signatureverifier

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.rakuten.tech.mobile.manifestconfig.annotations.ManifestConfig
import com.rakuten.tech.mobile.manifestconfig.annotations.MetaData
import io.github.rakutentech.signatureverifier.api.ApiClient
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher
import io.github.rakutentech.signatureverifier.verification.PublicKeyCache

/**
 * Fake ContentProvider that initializes the Signature Verifier SDK.
 **/
internal class SignatureVerifierInitProvider : ContentProvider() {

    @ManifestConfig
    internal interface App {

        /**
         * Base URL for the Public Key API.
         **/
        @MetaData(key = "io.github.rakutentech.signatureverifier.baseurl")
        fun baseUrl(): String
    }

    @Suppress("LongMethod")
    override fun onCreate(): Boolean {
        val context = context ?: return false

        val manifestConfig = AppManifestConfig(context)

        val client = ApiClient(
            baseUrl = manifestConfig.baseUrl(),
            context = context
        )

        SignatureVerifier.init(
            PublicKeyCache(
                keyFetcher = PublicKeyFetcher(client),
                context = context
            )
        )

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
}

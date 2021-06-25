package io.github.rakutentech.signatureverifier.api

import android.content.Context
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.IllegalArgumentException

internal class ApiClient(baseUrl: String, subscriptionKey: String, context: Context) {

    @Suppress("SpreadOperator")
    private val client = OkHttpClient.Builder()
        .cache(Cache(context.cacheDir,
            CACHE_SIZE
        ))
        .addNetworkInterceptor {
            val requestBuilder = it.request().newBuilder()
            requestBuilder.addHeader("apiKey", "ras-$subscriptionKey")
            it.proceed(requestBuilder.build())
        }
        .build()

    private val requestUrl = try {
        baseUrl.toHttpUrl()
    } catch (exception: IllegalArgumentException) {
        throw InvalidSignatureVerifierBaseUrlException(exception)
    }

    fun fetchPath(path: String, paramMap: Map<String, String>?): Response {
        val builder = requestUrl.newBuilder().addPathSegments(path)

        if (paramMap != null) {
            for ((k, v) in paramMap) {
                if (v.isNotEmpty() && k.isNotEmpty()) builder.addQueryParameter(k, v)
            }
        }

        return client.newCall(Request.Builder()
                .url(builder.build())
                .build()
        ).execute()
    }

    companion object {
        private const val CACHE_SIZE = 1024 * 1024 * 2L
    }
}

/**
 * Exception thrown when the value set in `AndroidManifest.xml` for
 * `io.github.rakutentech.signatureverifier.RSVKeyFetchEndpoint` is not a valid URL.
 */
class InvalidSignatureVerifierBaseUrlException(
    exception: IllegalArgumentException
) : IllegalArgumentException("An invalid URL was provided for the Signature Verifier base url.", exception)

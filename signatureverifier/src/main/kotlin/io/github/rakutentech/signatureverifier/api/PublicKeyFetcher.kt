package io.github.rakutentech.signatureverifier.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException

internal class PublicKeyFetcher(private val client: ApiClient) {

    @Throws(IOException::class)
    fun fetch(keyId: String): String {
        val response = client.fetchPath(keyId, null)

        if (!response.isSuccessful) {
            throw IOException("Unexpected response when fetching public key: $response")
        }

        val body = response.body!!.string() // Body is never null if request is successful

        return PublicKeyResponse.fromJsonString(body).ecKey ?: ""
    }
}

internal data class PublicKeyResponse(
    val id: String?,
    val ecKey: String?,
    val pemKey: String?
) {
    companion object {
        @SuppressWarnings("SwallowedException")
        fun fromJsonString(body: String): PublicKeyResponse {
            return try {
                Gson().fromJson(body, PublicKeyResponse::class.java)
            } catch (jex: JsonSyntaxException) {
                PublicKeyResponse("", "", "")
            }
        }
    }
}

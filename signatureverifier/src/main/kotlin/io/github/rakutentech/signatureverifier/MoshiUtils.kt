package io.github.rakutentech.signatureverifier

import com.squareup.moshi.Moshi

private val moshi = Moshi.Builder().build()

internal inline fun <reified T> jsonAdapter() =
    moshi.adapter<T>(T::class.java)
        .nonNull()
        .lenient()

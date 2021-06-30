---
layout: userguide
---

# Signature Verifier SDK for Android

Provides a security feature to verify any signed blob of data's authenticity after it has been downloaded for Android applications.

### This page covers:
* [Requirements](#requirements)
* [Getting Started](#getting-started)
* [SDK Logic](#sdk-logic)
* [Change Log](#changelog)

## <a name="requirements"></a> Requirements

### Supported Android Versions

This SDK supports Android API level 23 (Marshmallow) and above.

## <a name="getting-started"></a> Getting Started

### #1 Add dependency to your app's `build.gradle`

```groovy
repositories {
  mavenCentral()
}

dependency {
  implementation 'io.github.rakutentech.signatureverifier:signatureverifier:${latest_version}'
}
```

### #2 SDK usage

Create an instance of `SignatureVerifier` by providing the application `context`, endpoint URL for fetching public keys, and subscription key for authorization.
* an optional callback function can be set to receive details on any exceptions/errors encountered by the SDK.

If there are any issues encountered during initialization, the `init` API will return null.

* without callback
```kotlin
val instance = SignatureVerifier.init(
    context = context,
    baseUrl = "https://endpoint.url",
    subscriptionKey = "endpoint-subscription-key")
```

* with callback
```kotlin
val instance = SignatureVerifier.init(
    context = context,
    baseUrl = "https://endpoint.url",
    subscriptionKey = "endpoint-subscription-key"
    ) {
        Log.e(TAG, it.localizedMessage, it)
        // you can track or send this info to your analytics
    }
```

Then use the created instance to verify the signature inside a `Coroutine` (`verify` API is a suspend function).

The `verify` API requires 3 arguments, and will return `true` if `data` is verified:
1. `publicKeyId: String` - ID of public key to be fetched.
2. `data: InputStream` - Input stream of the data to be verified.
3. `signature: String` - Signature to be verified encoded in base64.

```kotlin
CoroutineScope(Dispatchers.Main).launch {
    val result = instance?.verify(
        publicKeyId = "<your_public_id>",
        data = stream,
        signature = "<your_signature>"
    )
}
```

## <a name="sdk-logic"></a> SDK Logic

### Caching

When a public key is fetched using the provided `publicKeyId`, it is encrypted then stored in the SDK cache.

If the same `publicKeyId` is used on another `verify` API call, the SDK will use the decrypted cached value for verification to avoid multiple backend requests.

On a rare case that the encryption algorithm fails, possibly due to some [`KeyStoreException`](https://developer.android.com/reference/java/security/KeyStoreException), the SDK will refetch the public key from the provided endpoint.

## <a name="changelog"></a> Changelog

### 1.0.0 (in-progress)
* Initial release.

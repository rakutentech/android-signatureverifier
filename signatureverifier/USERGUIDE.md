---
layout: userguide
---

# Signature Verifier SDK for Android

Provides a security feature to verify any signed blob of data's authenticity after it has been downloaded for Android applications.

### This page covers:
* [Requirements](#requirements)
* [Getting Started](#getting-started)
* [Advanced Features](#advanced-features)
* [Change Log](#changelog)

## <a name="requirements"></a> Requirements

### Supported Android Versions

This SDK supports Android API level 21 (Lollipop) and above.

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

Note: please use/enable R8 to avoid proguard issue with Moshi. For enabling and more details on R8, please refer to the [Android Developer documentation](https://developer.android.com/studio/build/shrink-code).

### #2 Configure SDK settings in AndroidManifest.xml

TBD (in-progress)

## <a name="advanced-features"></a> Advanced Features

TBD (in-progress)

## <a name="changelog"></a> Changelog

TBD (in-progress)

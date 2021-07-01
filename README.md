[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![CircleCI](https://circleci.com/gh/rakutentech/android-signatureverifier.svg?style=svg)](https://circleci.com/gh/rakutentech/android-signatureverifier)
[![codecov](https://codecov.io/gh/rakutentech/android-signatureverifier/branch/main/graph/badge.svg?)](https://codecov.io/gh/rakutentech/android-signatureverifier)

# Android Signature Verifier SDK
Provides a security feature to verify any signed blob of data's authenticity after it has been downloaded.

## How to build
This repository uses submodules for some configuration, so they must be initialized first.
```bash
$ git submodule update --init
$ ./gradlew assemble
```
## Continuous Integration and Deployment

[CircleCI](https://app.circleci.com/pipelines/github/rakutentech/android-signatureverifier) is used for building and testing the project for every pull request. It is also used for publishing the SDK.

We use jobs from CircleCI Orbs(see the [android-buildconfig](https://github.com/rakutentech/android-buildconfig/tree/master/circleci) repo): [android-sdk Orb](https://github.com/rakutentech/android-buildconfig/blob/master/circleci/android-sdk/README.md) 


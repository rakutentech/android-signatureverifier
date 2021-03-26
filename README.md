

# Android Signatureverifier SDK
Provides a security feature to verify data authenticity of the [Mini App](https://github.com/rakutentech/android-miniapp) ZIP file (source code) after it has been downloaded

## How to build
This repository uses submodules for some configuration, so they must be initialized first.
```bash
$ git submodule update --init
$ git submodule update
$ ./gradlew assemble
```
## Continuous Integration and Deployment

[CircleCI](https://app.circleci.com/pipelines/github/rakutentech/android-signatureverifier) is used for building and testing the project for every pull request. It is also used for publishing the SDK.

We use jobs from CircleCI Orbs(see the [android-buildconfig](https://github.com/rakutentech/android-buildconfig/tree/master/circleci) repo): [android-sdk Orb](https://github.com/rakutentech/android-buildconfig/blob/master/circleci/android-sdk/README.md) 


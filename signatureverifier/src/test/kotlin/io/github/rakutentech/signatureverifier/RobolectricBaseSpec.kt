package io.github.rakutentech.signatureverifier

import org.junit.Ignore
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@Ignore("base class")
open class RobolectricBaseSpec

interface IntegrationTests

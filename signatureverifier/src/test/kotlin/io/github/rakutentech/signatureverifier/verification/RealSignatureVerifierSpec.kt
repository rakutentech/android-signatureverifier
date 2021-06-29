package io.github.rakutentech.signatureverifier.verification

import io.github.rakutentech.signatureverifier.RealSignatureVerifier
import io.github.rakutentech.signatureverifier.RobolectricBaseSpec
import io.github.rakutentech.signatureverifier.api.PublicKeyFetcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.When
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
open class RealSignatureVerifierSpec : RobolectricBaseSpec() {

    /*
        The following commands were used to generate the public key and signature used in these tests

        ## Generate a private key
        $ openssl ecparam -name secp256r1 -out params.pem
        $ openssl ecparam -in params.pem -genkey -out privkey.pem

        ## Extract the public key
        $ openssl ec -in privkey.pem -pubout -out pubkey.pem
        $ cat pubkey.pem | openssl ec -pubin -text -noout
            read EC key
            Private-Key: (256 bit)
            pub:
                04:8d:b3:66:be:7a:82:19:cc:2d:70:4c:78:2e:1b:
                90:85:60:eb:3a:45:0f:62:02:21:2e:d5:e9:c5:a8:
                f2:9f:0b:92:26:ee:7a:bb:21:3b:e1:dd:e6:bb:ba:
                a0:11:4b:95:49:3a:20:51:4e:21:57:c7:2a:23:9e:
                9c:72:c1:34:86
            ASN1 OID: prime256v1
            NIST CURVE: P-256
        ## Use bytes from `pub:` section above and convert to base64 to get public key
        $ echo '04:8d:b3:66:be:7a:82:19:cc:2d:70:4c:78:2e:1b:90:85:60:eb:3a:45:0f:62:02:21:2e:d5:\
        e9:c5:a8:f2:9f:0b:92:26:ee:7a:bb:21:3b:e1:dd:e6:bb:ba:a0:11:4b:95:49:3a:20:51:4e:21:57:c7:\
        2a:23:9e:9c:72:c1:34:86' | tr -d : | xxd -r -ps | base64
            BI2zZr56ghnMLXBMeC4bkIVg6zpFD2ICIS7V6cWo8p8LkibuershO+Hd5ru6oBFLlUk6IFFOIVfHKiOenHLBNIY=

        ## Generate the signature
        $ openssl dgst -sha256 -sign privkey.pem -out signature.txt payload.txt
        $ openssl dgst -sha256 -verify pubkey.pem -signature signature.bin payload.txt
            Verified OK
        $ cat signature.bin | base64
            MEUCIHRXIgQhyASpyCP1Lg0ZSn2/bUbTq6U7jpKBa9Ow/1OTAiEA4jAq48uDgNl7UM7LmxhiRhPPNnTolokScTq5ijbp5fU
     */
    companion object {

        private const val KEY_ID = "test_id"
        private const val PUBLIC_KEY =
            "BI2zZr56ghnMLXBMeC4bkIVg6zpFD2ICIS7V6cWo8p8LkibuershO+Hd5ru6oBFLlUk" +
                    "6IFFOIVfHKiOenHLBNIY="
        private const val BODY = """{"testKey": "test_value"}"""
        private const val SIGNATURE =
            "MEUCIHRXIgQhyASpyCP1Lg0ZSn2/bUbTq6U7jpKBa9Ow/1OTAiEA4jAq48uDgNl7UM7" +
                    "LmxhiRhPPNnTolokScTq5ijbp5fU="
    }

    private val mockPublicKeyCache = Mockito.mock(PublicKeyCache::class.java)
    private val mockFetcher = Mockito.mock(PublicKeyFetcher::class.java)

    @Before
    fun setup() {
        When calling mockPublicKeyCache[KEY_ID] itReturns PUBLIC_KEY
    }

    @Test
    fun `should verify the signature`() = runBlockingTest {
        val verifier = RealSignatureVerifier(mockPublicKeyCache, TestCoroutineDispatcher())

        verifier.verify(
            KEY_ID,
            BODY.byteInputStream(),
            SIGNATURE
        ) shouldBeEqualTo true
    }

    @Test
    fun `should not verify the signature when message has been modified`() = runBlockingTest {
        val verifier = RealSignatureVerifier(mockPublicKeyCache, TestCoroutineDispatcher())

        verifier.verify(
            KEY_ID,
            "wrong message".byteInputStream(),
            SIGNATURE
        ) shouldBeEqualTo false
    }
}

package net.wiringbits.util

import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper, empty}
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class TokensHelperSpec extends AnyWordSpec {

  "doHMACSHA1" should {
    "create a valid hmac" in {
      val uuid = UUID.randomUUID()
      val secretKey = "test"
      val hmac = TokensHelper.doHMACSHA1(value = uuid.toString.getBytes, secretKey = secretKey)

      hmac mustNot be(empty)
    }
  }

  "isSignatureValid" should {
    "return true when the data doesn't changes" in {
      val uuid = UUID.randomUUID()
      val secretKey = "test"
      val hmac = TokensHelper.doHMACSHA1(value = uuid.toString.getBytes, secretKey = secretKey)

      TokensHelper.isSignatureValid(tokensSecret = secretKey, digest = hmac, data = uuid.toString.getBytes) must be(
        true
      )
    }

    "return false when the data changes" in {
      val secretKey = "test"
      val hmac = TokensHelper.doHMACSHA1(value = UUID.randomUUID.toString.getBytes, secretKey = secretKey)

      TokensHelper.isSignatureValid(
        tokensSecret = secretKey,
        digest = hmac,
        data = UUID.randomUUID.toString.getBytes
      ) must be(false)
    }
  }

}

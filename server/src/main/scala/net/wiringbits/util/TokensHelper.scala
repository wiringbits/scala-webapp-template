package net.wiringbits.util

import javax.xml.bind.DatatypeConverter

object TokensHelper {

  def doHMACSHA1(value: Array[Byte], secretKey: String): String = {
    import javax.crypto.Mac
    import javax.crypto.spec.SecretKeySpec
    val signingKey = new SecretKeySpec(secretKey.getBytes, "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(signingKey)
    val rawHmac = mac.doFinal(value)
    // TODO: Use a compatible converter
    // https://stackoverflow.com/a/67127499/14738484
    DatatypeConverter.printHexBinary(rawHmac)
  }

  def isSignatureValid(tokensSecret: String, digest: String, data: Array[Byte]): Boolean = {
    val ourDigest = doHMACSHA1(data, tokensSecret)
    ourDigest equalsIgnoreCase digest
  }
}

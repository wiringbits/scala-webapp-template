/*
 * scalajs-fake-insecure-java-securerandom (https://github.com/scala-js/scala-js-fake-insecure-java-securerandom)
 *
 * Copyright EPFL.
 *
 * Licensed under Apache License 2.0
 * (https://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package java.security

import scala.scalajs.js
import scala.scalajs.js.typedarray._

// DISCLAIMER: This is almost identical to the official library https://github.com/scala-js/scala-js-fake-insecure-java-securerandom
// There was the need to apply a patch that won't be accepted by the upstream library, given that this is used
// only for tests, it shouldn't be a problem to keep the patch.
//
// The seed in java.util.Random will be unused, so set to 0L instead of having to generate one
class SecureRandom() extends java.util.Random(0L) {
  // Make sure to resolve the appropriate function no later than the first instantiation
  private val getRandomValuesFun = SecureRandom.getRandomValuesFun

  /* setSeed has no effect. For cryptographically secure PRNGs, giving a seed
   * can only ever increase the entropy. It is never allowed to decrease it.
   * Given that we don't have access to an API to strengthen the entropy of the
   * underlying PRNG, it's fine to ignore it instead.
   *
   * Note that the doc of `SecureRandom` says that it will seed itself upon
   * first call to `nextBytes` or `next`, if it has not been seeded yet. This
   * suggests that an *initial* call to `setSeed` would make a `SecureRandom`
   * instance deterministic. Experimentally, this does not seem to be the case,
   * however, so we don't spend extra effort to make that happen.
   */
  override def setSeed(x: Long): Unit = ()

  override def nextBytes(bytes: Array[Byte]): Unit = {
    val len = bytes.length
    val buffer = new Int8Array(len)
    getRandomValuesFun(buffer)
    var i = 0
    while (i != len) {
      bytes(i) = buffer(i)
      i += 1
    }
  }

  override protected final def next(numBits: Int): Int = {
    if (numBits <= 0) {
      0 // special case because the formula on the last line is incorrect for numBits == 0
    } else {
      val buffer = new Int32Array(1)
      getRandomValuesFun(buffer)
      val rand32 = buffer(0)
      rand32 & (-1 >>> (32 - numBits)) // Clear the (32 - numBits) higher order bits
    }
  }
}

object SecureRandom {
  private lazy val getRandomValuesFun: js.Function1[ArrayBufferView, Unit] = {
    if (
      js.typeOf(js.Dynamic.global.crypto) != "undefined" &&
      js.typeOf(js.Dynamic.global.crypto.getRandomValues) == "function"
    ) {
      { (buffer: ArrayBufferView) =>
        js.Dynamic.global.crypto.getRandomValues(buffer)
        ()
      }
    } else if (js.typeOf(js.Dynamic.global.require) == "function") {
      try {
        val crypto = js.Dynamic.global.require("crypto")
        if (js.typeOf(crypto.randomFillSync) == "function") {
          { (buffer: ArrayBufferView) =>
            /** This part differs from the official implementation because it catches runtime exceptions
              *
              * This was necessary because webpack seems to be polluting our runtime libraries with one that breaks in
              * the tests.
              */
            try {
              crypto.randomFillSync(buffer)
            } catch {
              case _: Throwable => insecureDefault(buffer)
            }
            ()
          }
        } else {
          insecureDefault
        }
      } catch {
        case _: Throwable =>
          insecureDefault
      }
    } else {
      insecureDefault
    }
  }

  private def insecureDefault: js.Function1[ArrayBufferView, Unit] = {
    val insecureRandom = new java.util.Random()

    { (buffer: ArrayBufferView) =>
      val asInt8Array = new Int8Array(buffer.buffer, buffer.byteOffset, buffer.byteLength)
      val len = asInt8Array.length
      val arrayBuffer = new Array[Byte](len)
      insecureRandom.nextBytes(arrayBuffer)
      var i = 0
      while (i != len) {
        asInt8Array(i) = arrayBuffer(i)
        i += 1
      }
    }
  }
}

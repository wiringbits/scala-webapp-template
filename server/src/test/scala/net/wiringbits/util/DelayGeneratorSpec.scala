package net.wiringbits.util

import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec

class DelayGeneratorSpec extends AnyWordSpec {

  "createDelay" should {
    "create an exponential sequence in a linear sequence of numbers" in {
      val expected = List(1, 2, 4, 8, 16)

      val response = expected.indices.map(x => DelayGenerator.createDelay(x)).toList

      response must be(expected)
    }
  }
}

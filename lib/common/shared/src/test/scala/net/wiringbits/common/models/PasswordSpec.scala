package net.wiringbits.common.models

import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class PasswordSpec extends AnyWordSpec {

  val valid = List(
    "12345678",
    "aaabbbcc",
    "..121l2.1.2o9z9n23 voi109"
  )

  val invalid = List(
    "...11..",
    "",
    "1j190u"
  )

  "validate" should {
    valid.foreach { input =>
      s"accept valid values: $input" in {
        Password.validate(input).isValid must be(true)
      }
    }

    invalid.foreach { input =>
      s"reject invalid values: $input" in {
        Password.validate(input).isValid must be(false)
      }
    }
  }
}

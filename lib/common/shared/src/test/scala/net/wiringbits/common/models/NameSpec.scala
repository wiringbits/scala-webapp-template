package net.wiringbits.common.models

import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class NameSpec extends AnyWordSpec {

  val valid = List(
    "ale",
    "jo",
    "jorge julian"
  )

  val invalid = List(
    ".",
    "",
    "a"
  )

  "validate" should {
    valid.foreach { input =>
      s"accept valid values: $input" in {
        Name.validate(input).isValid must be(true)
      }
    }

    invalid.foreach { input =>
      s"reject invalid values: $input" in {
        Name.validate(input).isValid must be(false)
      }
    }
  }
}

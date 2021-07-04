package net.wiringbits.api.utils

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers._

class ValidatorSpec extends AnyWordSpec {
  "isValidEmail" should {
    val valid = List(
      "alexis@wiringbits.net",
      "a@xe.com",
      "ejemplo@goo.gl",
      "ejemplo+aqui@e.io"
    )
    // TODO: Uncomment cases when the validator gets fixed
    val invalid = List(
//      "alexis@wiringbits.net.",
//      "alexis@wiringbits.net a@xe.net",
//      "esto,noes@unemail",
//      "esto tampoco@es",
//      "ejemplo@goo",
      "@xe.com",
      "hello@",
      ".",
      ""
    )

    valid.foreach { input =>
      s"accept valid email: $input" in {
        Validator.isValidEmail(input) must be(true)
      }
    }

    invalid.foreach { input =>
      s"reject invalid email: $input" in {
        Validator.isValidEmail(input) must be(false)
      }
    }
  }
}

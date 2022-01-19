package net.wiringbits.common.models

import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class EmailSpec extends AnyWordSpec {

  val valid = List(
    "alexis@wiringbits.net",
    "a@xe.com",
    "ejemplo@goo.gl",
    "ejemplo+aqui@e.io",
    "one_mail@test.com",
    "valid.mail@test.xs",
    "valid_@gf.com",
    "test@gmail.co.au"
  )

  val invalid = List(
    "alexis@wiringbits.net.",
    "alexis@wiringbits.net a@xe.net",
    "esto,noes@unemail",
    "esto tampoco@es",
    "@xe.com",
    "hello@",
    "ejemplo@goo",
    ".",
    ""
  )

  "validate" should {
    valid.foreach { input =>
      s"accept valid values: $input" in {
        Email.validate(input).isValid must be(true)
      }
    }

    invalid.foreach { input =>
      s"reject invalid values: $input" in {
        Email.validate(input).isValid must be(false)
      }
    }
  }
}

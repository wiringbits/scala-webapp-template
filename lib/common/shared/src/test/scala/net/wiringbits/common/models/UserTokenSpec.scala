package net.wiringbits.common.models

import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class UserTokenSpec extends AnyWordSpec {

  "validate" should {
    "succeed when there's two valid UUIDs and one underscore" in {
      val valid = s"${UUID.randomUUID()}_${UUID.randomUUID()}"
      UserToken.validate(valid).isDefined must be(true)
    }

    s"fail when the string is not a valid UUID" in {
      val invalid = "wiringbits"
      UserToken.validate(invalid).isDefined must be(false)
    }

    s"fail when the string is not a valid UUID and there's an underscore" in {
      val invalid = "wiringbits_wiringbits"
      UserToken.validate(invalid).isDefined must be(false)
    }

    s"fail when there's zero underscores in the string" in {
      val invalid = UUID.randomUUID.toString
      UserToken.validate(invalid).isDefined must be(false)
    }

    s"fail when there's more than two underscores in the string" in {
      val invalid = s"${UUID.randomUUID()}_${UUID.randomUUID()}_${UUID.randomUUID()}"
      UserToken.validate(invalid).isDefined must be(false)
    }

    s"fail when there's an underscore after the UUID" in {
      val invalid = s"${UUID.randomUUID()}_"
      UserToken.validate(invalid).isDefined must be(false)
    }
  }
}

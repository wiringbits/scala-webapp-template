package net.wiringbits.api.forms

import net.wiringbits.common.core.ValidationResult
import org.scalatest.OptionValues._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class FormFieldSpec extends AnyWordSpec {

  private val pristineMandatoryField = new FormField[String](
    "test",
    required = true
  )

  private val pristineOptionalField = new FormField[Option[String]](
    "test",
    required = false
  )

  "updated" should {
    "set the new value without changing anything else" in {
      val result = pristineMandatoryField.updated(ValidationResult.Valid("test", "test"))
      result.value.isDefined must be(true)
      result.required must be(pristineMandatoryField.required)
      result.name must be(pristineMandatoryField.name)
      result.label must be(pristineMandatoryField.label)
      result.`type` must be(pristineMandatoryField.`type`)
    }
  }

  "errorMsg" should {
    "return an error when the field is required but it hasn't been provided" in {
      val result = pristineMandatoryField.errorMsg
      result.value must be("Required")
    }

    "return an error when the field is required but it is invalid" in {
      val error = "just an error"

      val result = pristineMandatoryField
        .updated(ValidationResult.Invalid("hello", error))
        .errorMsg

      result.value must be(error)
    }

    "return an error when the field is optional but it is invalid" in {
      val error = "just an error"

      val result = pristineOptionalField
        .updated(ValidationResult.Invalid("hello", error))
        .errorMsg

      result.value must be(error)
    }

    "return None when the field is optional and it isn't provided" in {
      val result = pristineOptionalField.errorMsg

      result must be(empty)

    }

    "return None when the field is optional and it is valid" in {
      val result = pristineOptionalField
        .updated(ValidationResult.Valid("hello", Some("hello")))
        .errorMsg

      result must be(empty)
    }

    "return None when the field is required and it is valid" in {
      val result = pristineMandatoryField
        .updated(ValidationResult.Valid("hello", "hello"))
        .errorMsg

      result must be(empty)
    }
  }
}

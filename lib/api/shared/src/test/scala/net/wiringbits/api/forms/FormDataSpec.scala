package net.wiringbits.api.forms

import net.wiringbits.common.core.ValidationResult
import org.scalatest.OptionValues._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class FormDataSpec extends AnyWordSpec {

  private val pristineMandatoryField = new FormField[String](
    "test",
    required = true
  )

  private val pristineOptionalField = new FormField[Option[String]](
    "test",
    required = false
  )

  private val validMandatoryField = pristineMandatoryField
    .updated(ValidationResult.Valid("hello", "hello"))

  private val invalidFieldError = "just an error"

  private val invalidMandatoryField = pristineMandatoryField
    .updated(ValidationResult.Invalid("hello", invalidFieldError))

  private val invalidOptionalField = pristineOptionalField
    .updated(ValidationResult.Invalid("hello", invalidFieldError))

  private val filledOptionalField = pristineOptionalField
    .updated(ValidationResult.Valid("hello", Some("hello")))

  "fieldsError" should {
    "return an error when there are unfilled mandatory fields" in {
      val expected = "The mandatory fields need to be filled"
      val data = FakeFormData(fields =
        List(pristineOptionalField, validMandatoryField, filledOptionalField, pristineMandatoryField)
      )
      data.fieldsError.value must be(expected)
    }

    "return an error when there are invalid mandatory fields" in {
      val expected = "The mandatory fields need to be filled"
      val data = FakeFormData(fields =
        List(pristineOptionalField, validMandatoryField, filledOptionalField, invalidMandatoryField)
      )
      data.fieldsError.value must be(expected)
    }

    "return an error when there are invalid optional fields" in {
      val expected = "The optional fields need to be either absent or be valid"
      val data = FakeFormData(fields =
        List(pristineOptionalField, validMandatoryField, filledOptionalField, invalidOptionalField)
      )
      data.fieldsError.value must be(expected)
    }

    "return None when all fields are provided and valid" in {
      val data = FakeFormData(fields = List(pristineOptionalField, validMandatoryField, filledOptionalField))
      data.fieldsError must be(empty)
    }
  }

  "isValid" should {
    "return true when all fields are valid and there are no extra validation errors" in {
      val data = FakeFormData(fields = List(pristineOptionalField, validMandatoryField, filledOptionalField))
      data.isValid must be(true)
    }

    "return false when a mandatory field has errors" in {
      val data = FakeFormData(fields =
        List(pristineOptionalField, validMandatoryField, filledOptionalField, invalidMandatoryField)
      )
      data.isValid must be(false)
    }
    "return false when an optional field has errors" in {
      val data = FakeFormData(fields =
        List(pristineOptionalField, validMandatoryField, filledOptionalField, invalidOptionalField)
      )
      data.isValid must be(false)
    }

    "return false when there are extra validation errors" in {
      val data = FakeFormData(
        formValidationErrors = List("whoops"),
        fields = List(pristineOptionalField, validMandatoryField, filledOptionalField)
      )

      data.isValid must be(false)
    }
  }
}

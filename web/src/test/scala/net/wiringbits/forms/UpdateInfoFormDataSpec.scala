package net.wiringbits.forms

import net.wiringbits.common.models.{Email, Name}
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class UpdateInfoFormDataSpec extends AnyWordSpec {

  private val initialForm = UpdateInfoFormData.initial(
    nameLabel = "name",
    emailLabel = "Email"
  )

  private val validForm = initialForm
    .copy(
      name = initialForm.name.updated(Name.validate("someone")),
      email = initialForm.email.updated(Email.validate("hello@test.com"))
    )

  private val allDataInvalidForm = initialForm
    .copy(
      name = initialForm.name.updated(Name.validate("x")),
      email = initialForm.email.updated(Email.validate("x@"))
    )

  "fields" should {
    "return the expected fields" in {
      val expected = List("name")
      initialForm.fields.map(_.name).toSet must be(expected.toSet)
    }
  }

  "formValidationErrors" should {
    "return no errors when everything mandatory is correct" in {
      val result = validForm.formValidationErrors
      result must be(empty)
    }

    "return all errors" in {
      allDataInvalidForm.formValidationErrors.size must be(1)
    }
  }

  "submitRequest" should {

    "return a request when the data is valid" in {
      val result = validForm.submitRequest
      result.isDefined must be(true)
    }

    "return None when the data is not valid" in {
      val result = allDataInvalidForm.submitRequest
      result.isDefined must be(false)
    }
  }
}

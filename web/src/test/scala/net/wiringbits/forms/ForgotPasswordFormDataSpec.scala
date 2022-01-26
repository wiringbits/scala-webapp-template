package net.wiringbits.forms

import net.wiringbits.common.models.{Captcha, Email}
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class ForgotPasswordFormDataSpec extends AnyWordSpec {

  private val initialForm = ForgotPasswordFormData.initial(
    emailLabel = "Email"
  )

  private val validForm = initialForm
    .copy(
      email = initialForm.email.updated(Email.validate("hello@test.com")),
      captcha = Some(Captcha.trusted("test"))
    )

  private val allDataInvalidForm = initialForm
    .copy(
      email = initialForm.email.updated(Email.validate("x@"))
    )

  "fields" should {
    "return the expected fields" in {
      val expected = List("email")
      initialForm.fields.map(_.name).toSet must be(expected.toSet)
    }
  }

  "formValidationErrors" should {
    "return no errors when everything mandatory is correct" in {
      val result = validForm.formValidationErrors
      result must be(empty)
    }

    "return all errors" in {
      allDataInvalidForm.formValidationErrors.size must be(2)
    }
  }

  "submitRequest" should {

    "return a request when the emmail is valid" in {
      val result = validForm.submitRequest
      result.isDefined must be(true)
    }

    "return None when the data is not valid" in {
      val form = validForm
      val invalidEmail = form.copy(email = allDataInvalidForm.email)

      List(invalidEmail).foreach { form =>
        form.submitRequest.isDefined must be(false)
      }
    }
  }
}

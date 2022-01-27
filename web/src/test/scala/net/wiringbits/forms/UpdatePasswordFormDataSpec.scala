package net.wiringbits.forms

import net.wiringbits.common.models.Password
import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper, empty}
import org.scalatest.wordspec.AnyWordSpec

class UpdatePasswordFormDataSpec extends AnyWordSpec {

  private val initialForm = UpdatePasswordFormData.initial(
    oldPasswordLabel = "Old password",
    passwordLabel = "Password",
    repeatPasswordLabel = "Repeat password"
  )

  private val validForm = initialForm
    .copy(
      oldPassword = initialForm.oldPassword.updated(Password.validate("1234567890")),
      password = initialForm.password.updated(Password.validate("123456789")),
      repeatPassword = initialForm.repeatPassword.updated(Password.validate("123456789"))
    )

  private val allDataInvalidForm = initialForm
    .copy(
      oldPassword = initialForm.oldPassword.updated(Password.validate("x")),
      password = initialForm.password.updated(Password.validate("x")),
      repeatPassword = initialForm.repeatPassword.updated(Password.validate("x"))
    )

  "fields" should {
    "return the expected fields" in {
      val expected = List("oldPassword", "password", "repeatPassword")
      initialForm.fields.map(_.name).toSet must be(expected.toSet)
    }
  }

  "formValidationErrors" should {
    "return no errors when everything mandatory is correct" in {
      val result = validForm.formValidationErrors
      result must be(empty)
    }

    "return error when the password do not match" in {
      val form = validForm.copy(
        password = initialForm.password.updated(Password.validate("123456789")),
        repeatPassword = initialForm.repeatPassword.updated(Password.validate("123456788"))
      )

      form.formValidationErrors must be(List("The passwords does not match"))
    }

    "return no password match error when password isn't valid" in {
      val form = validForm.copy(
        password = initialForm.password.updated(Password.validate("19")),
        repeatPassword = initialForm.repeatPassword.updated(Password.validate("123456789"))
      )

      form.formValidationErrors.contains("The passwords does not match") must be(false)
    }

    "return no password match error when repeatPassword isn't valid" in {
      val form = validForm.copy(
        password = initialForm.password.updated(Password.validate("123456789")),
        repeatPassword = initialForm.repeatPassword.updated(Password.validate("12"))
      )

      form.formValidationErrors.contains("The passwords does not match") must be(false)
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
      val form = validForm
      val invalidOldPassword = form.copy(oldPassword = allDataInvalidForm.oldPassword)
      val invalidPassword = form.copy(password = allDataInvalidForm.password)

      List(invalidOldPassword, invalidPassword).foreach { form =>
        form.submitRequest.isDefined must be(false)
      }
    }
  }
}

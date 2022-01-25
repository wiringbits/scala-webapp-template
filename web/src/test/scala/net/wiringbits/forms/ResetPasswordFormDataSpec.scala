package net.wiringbits.forms

import net.wiringbits.common.models.{Password, UserToken}
import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper, empty}
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class ResetPasswordFormDataSpec extends AnyWordSpec {
  private val initialForm = ResetPasswordFormData.initial(
    passwordLabel = "Password",
    repeatPasswordLabel = "Repeat password",
    token = Some(UserToken(UUID.randomUUID, UUID.randomUUID))
  )

  private val validForm = initialForm
    .copy(
      password = initialForm.password.updated(Password.validate("123456789")),
      repeatPassword = initialForm.repeatPassword.updated(Password.validate("123456789")),
      token = Some(UserToken(UUID.randomUUID, UUID.randomUUID))
    )

  private val allDataInvalidForm = initialForm
    .copy(
      password = initialForm.password.updated(Password.validate("x")),
      repeatPassword = initialForm.repeatPassword.updated(Password.validate("x")),
      token = None
    )

  "fields" should {
    "return the expected fields" in {
      val expected = List("password", "repeatPassword")
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
      allDataInvalidForm.formValidationErrors.size must be(2)
    }
  }

  "submitRequest" should {
    "return None when the data is not valid" in {
      val form = validForm
      val invalidPassword = form.copy(password = allDataInvalidForm.password)

      List(invalidPassword).foreach { form =>
        form.submitRequest.isDefined must be(false)
      }
    }
  }
}

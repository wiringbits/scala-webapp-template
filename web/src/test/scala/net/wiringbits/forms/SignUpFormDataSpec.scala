package net.wiringbits.forms

import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class SignUpFormDataSpec extends AnyWordSpec {

  private val initialForm = SignUpFormData.initial(
    nameLabel = "name",
    emailLabel = "Email",
    passwordLabel = "Password",
    repeatPasswordLabel = "Repeat password"
  )

  private val validForm = initialForm
    .copy(
      name = initialForm.name.updated(Name.validate("someone")),
      email = initialForm.email.updated(Email.validate("hello@test.com")),
      password = initialForm.password.updated(Password.validate("123456789")),
      repeatPassword = initialForm.repeatPassword.updated(Password.validate("123456789")),
      captcha = Some(Captcha.trusted("test"))
    )

  private val allDataInvalidForm = initialForm
    .copy(
      name = initialForm.name.updated(Name.validate("x")),
      email = initialForm.email.updated(Email.validate("x@")),
      password = initialForm.password.updated(Password.validate("x")),
      repeatPassword = initialForm.repeatPassword.updated(Password.validate("x")),
      captcha = None
    )

  "fields" should {
    "return the expected fields" in {
      val expected = List("name", "email", "password", "repeatPassword")
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
      val invalidName = form.copy(name = allDataInvalidForm.name)
      val invalidEmail = form.copy(email = allDataInvalidForm.email)
      val invalidPassword = form.copy(password = allDataInvalidForm.password)

      List(invalidName, invalidEmail, invalidPassword).foreach { form =>
        form.submitRequest.isDefined must be(false)
      }
    }
  }
}

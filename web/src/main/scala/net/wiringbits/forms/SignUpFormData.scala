package net.wiringbits.forms

import net.wiringbits.api.models.CreateUser
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class SignUpFormData(
    name: FormField[Name],
    email: FormField[Email],
    password: FormField[Password],
    repeatPassword: FormField[Password],
    captcha: Option[Captcha] = None
) extends FormData[CreateUser.Request] {
  override def fields: List[FormField[_]] = List(name, email, password, repeatPassword)

  override def formValidationErrors: List[String] = {
    // the error is rendered only when both fields are provided
    val passwordMatchesError = (for {
      password1 <- password.valueOpt
      password2 <- repeatPassword.valueOpt
    } yield password1 != password2)
      .filter(identity)
      .map(_ => "The passwords does not match")

    val emptyCaptcha = Option.when(captcha.isEmpty)("Complete the captcha")

    List(
      fieldsError,
      passwordMatchesError,
      emptyCaptcha
    ).flatten
  }

  override def submitRequest: Option[CreateUser.Request] = {
    val formData = this
    for {
      name <- formData.name.valueOpt
      email <- formData.email.valueOpt
      password <- formData.password.valueOpt
      captcha <- formData.captcha
    } yield CreateUser.Request(
      name,
      email,
      password,
      captcha
    )
  }
}

object SignUpFormData {
  // TODO: Implement "Complete captcha message" from i18nMessages like ResendVerifyEmailFormData

  def initial(
      nameLabel: String,
      emailLabel: String,
      passwordLabel: String,
      repeatPasswordLabel: String
  ): SignUpFormData = SignUpFormData(
    name = new FormField(label = nameLabel, name = "name", required = true),
    email = new FormField(label = emailLabel, name = "email", required = true, `type` = "email"),
    password = new FormField(label = passwordLabel, name = "password", required = true, `type` = "password"),
    repeatPassword =
      new FormField(label = repeatPasswordLabel, name = "repeatPassword", required = true, `type` = "password")
  )
}

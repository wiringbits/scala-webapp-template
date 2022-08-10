package net.wiringbits.forms

import net.wiringbits.api.models.Login
import net.wiringbits.common.models.{Captcha, Email, Password}
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class SignInFormData(
    email: FormField[Email],
    password: FormField[Password],
    captcha: Option[Captcha] = None
) extends FormData[Login.Request] {
  override def fields: List[FormField[_]] = List(email, password)

  override def formValidationErrors: List[String] = {
    val emptyCaptcha = Option.when(captcha.isEmpty)("Complete the captcha")

    List(
      fieldsError,
      emptyCaptcha
    ).flatten
  }

  override def submitRequest: Option[Login.Request] = {
    val formData = this
    for {
      email <- formData.email.valueOpt
      password <- formData.password.valueOpt
      captcha <- formData.captcha
    } yield Login.Request(
      email,
      password,
      captcha
    )
  }
}

object SignInFormData {
  // TODO: Implement "Complete captcha message" from i18nMessages like ResendVerifyEmailFormData

  def initial(
      emailLabel: String,
      passwordLabel: String
  ): SignInFormData = SignInFormData(
    email = new FormField(label = emailLabel, name = "email", required = true, `type` = "email"),
    password = new FormField(label = passwordLabel, name = "password", required = true, `type` = "password")
  )
}

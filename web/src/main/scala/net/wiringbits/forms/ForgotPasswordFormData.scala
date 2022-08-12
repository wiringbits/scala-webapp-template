package net.wiringbits.forms

import net.wiringbits.api.models.ForgotPassword
import net.wiringbits.common.models.{Captcha, Email}
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class ForgotPasswordFormData(
    email: FormField[Email],
    captcha: Option[Captcha] = None
) extends FormData[ForgotPassword.Request] {
  override def fields: List[FormField[_]] = List(email)

  override def formValidationErrors: List[String] = {
    val captchaError = Option.when(captcha.isEmpty)("Complete the captcha")

    List(
      fieldsError,
      captchaError
    ).flatten
  }

  override def submitRequest: Option[ForgotPassword.Request] = {
    val formData = this
    for {
      email <- formData.email.valueOpt
      captcha <- formData.captcha
    } yield ForgotPassword.Request(
      email = email,
      captcha = captcha
    )
  }
}

object ForgotPasswordFormData {
  // TODO: Implement "Complete captcha message" from i18nMessages like ResendVerifyEmailFormData

  def initial(
      emailLabel: String
  ): ForgotPasswordFormData = ForgotPasswordFormData(
    email = new FormField(label = emailLabel, name = "email", required = true, `type` = "email")
  )
}

package net.wiringbits.forms

import net.wiringbits.api.models.SendEmailVerificationToken
import net.wiringbits.common.models.{Captcha, Email}
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class ResendVerifyEmailFormData(
    email: FormField[Email],
    captcha: Option[Captcha] = None
) extends FormData[SendEmailVerificationToken.Request] {
  override def fields: List[FormField[_]] = List(email)

  override def formValidationErrors: List[String] = {
    val emptyCaptcha = Option.when(captcha.isEmpty)("Complete the captcha")

    List(
      fieldsError,
      emptyCaptcha
    ).flatten
  }

  override def submitRequest: Option[SendEmailVerificationToken.Request] = {
    val formData = this
    for {
      email <- formData.email.valueOpt
      captcha <- formData.captcha
    } yield SendEmailVerificationToken.Request(
      email,
      captcha
    )
  }
}

object ResendVerifyEmailFormData {
  def initial(
      emailLabel: String
  ): ResendVerifyEmailFormData = ResendVerifyEmailFormData(
    email = new FormField[Email](label = emailLabel, name = "email", required = true, `type` = "email")
  )
}

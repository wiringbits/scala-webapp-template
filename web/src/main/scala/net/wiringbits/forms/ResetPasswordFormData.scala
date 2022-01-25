package net.wiringbits.forms

import net.wiringbits.api.models.ResetPassword
import net.wiringbits.common.models.{Password, UserToken}
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class ResetPasswordFormData(
    password: FormField[Password],
    repeatPassword: FormField[Password],
    token: Option[UserToken]
) extends FormData[ResetPassword.Request] {
  override def fields: List[FormField[_]] = List(password, repeatPassword)

  override def formValidationErrors: List[String] = {
    val isTokenDefined =
      Option.when(token.isEmpty)("The token doesn't exists")

    // the error is rendered only when both fields are provided
    val passwordMatchesError = (for {
      password1 <- password.valueOpt
      password2 <- repeatPassword.valueOpt
    } yield password1 != password2)
      .filter(identity)
      .map(_ => "The passwords does not match")

    List(
      fieldsError,
      passwordMatchesError,
      isTokenDefined
    ).flatten
  }

  override def submitRequest: Option[ResetPassword.Request] = {
    val formData = this
    for {
      password <- formData.password.valueOpt
      token <- formData.token
    } yield ResetPassword.Request(
      token = token,
      password = password
    )
  }
}

object ResetPasswordFormData {

  def initial(
      passwordLabel: String,
      repeatPasswordLabel: String,
      token: Option[UserToken]
  ): ResetPasswordFormData = ResetPasswordFormData(
    password = new FormField(label = passwordLabel, name = "password", required = true, `type` = "password"),
    repeatPassword =
      new FormField(label = repeatPasswordLabel, name = "repeatPassword", required = true, `type` = "password"),
    token = token
  )
}

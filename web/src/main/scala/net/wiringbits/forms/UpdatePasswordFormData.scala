package net.wiringbits.forms

import net.wiringbits.api.models.UpdatePassword
import net.wiringbits.common.models._
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class UpdatePasswordFormData(
    oldPassword: FormField[Password],
    password: FormField[Password],
    repeatPassword: FormField[Password]
) extends FormData[UpdatePassword.Request] {
  override def fields: List[FormField[_]] = List(oldPassword, password, repeatPassword)

  override def formValidationErrors: List[String] = {
    // the error is rendered only when both fields are provided
    val passwordMatchesError = (for {
      password1 <- password.valueOpt
      password2 <- repeatPassword.valueOpt
    } yield password1 != password2)
      .filter(identity)
      .map(_ => "The passwords does not match")

    List(
      fieldsError,
      passwordMatchesError
    ).flatten
  }

  override def submitRequest: Option[UpdatePassword.Request] = {
    val formData = this
    for {
      oldPassword <- formData.oldPassword.valueOpt
      password <- formData.password.valueOpt
    } yield UpdatePassword.Request(
      oldPassword,
      password
    )
  }
}

object UpdatePasswordFormData {

  def initial(
      oldPasswordLabel: String,
      passwordLabel: String,
      repeatPasswordLabel: String
  ): UpdatePasswordFormData = UpdatePasswordFormData(
    oldPassword = new FormField(label = oldPasswordLabel, name = "oldPassword", required = true, `type` = "password"),
    password = new FormField(label = passwordLabel, name = "password", required = true, `type` = "password"),
    repeatPassword =
      new FormField(label = repeatPasswordLabel, name = "repeatPassword", required = true, `type` = "password")
  )
}

package net.wiringbits.forms

import net.wiringbits.api.forms.{FormData, FormField}
import net.wiringbits.api.models.CreateUser
import net.wiringbits.common.models.{Email, Name, Password}

case class SignUpFormData(
    name: FormField[Name],
    email: FormField[Email],
    password: FormField[Password],
    repeatPassword: FormField[Password]
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

    List(
      fieldsError,
      passwordMatchesError
    ).flatten
  }

  override def submitRequest: Option[CreateUser.Request] = {
    val formData = this
    for {
      name <- formData.name.valueOpt
      email <- formData.email.valueOpt
      password <- formData.password.valueOpt
    } yield CreateUser.Request(
      name,
      email,
      password
    )
  }
}

object SignUpFormData {

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

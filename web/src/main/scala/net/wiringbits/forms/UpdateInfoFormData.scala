package net.wiringbits.forms

import net.wiringbits.api.models.UpdateUser
import net.wiringbits.common.models._
import net.wiringbits.webapp.common.validators.ValidationResult
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class UpdateInfoFormData(
    name: FormField[Name],
    email: FormField[Email]
) extends FormData[UpdateUser.Request] {
  override def fields: List[FormField[_]] = List(name)

  override def formValidationErrors: List[String] = {
    List(
      fieldsError
    ).flatten
  }

  override def submitRequest: Option[UpdateUser.Request] = {
    val formData = this
    for {
      name <- formData.name.valueOpt
    } yield UpdateUser.Request(
      name
    )
  }
}

object UpdateInfoFormData {

  def initial(
      nameLabel: String,
      nameInitialValue: Name,
      emailLabel: String,
      emailValue: Email
  ): UpdateInfoFormData = UpdateInfoFormData(
    name = new FormField(
      label = nameLabel,
      name = "name",
      value = Some(ValidationResult.Valid(nameInitialValue.string, nameInitialValue))
    ),
    email = new FormField(
      label = emailLabel,
      name = "email",
      `type` = "email",
      value = Some(ValidationResult.Valid(emailValue.string, emailValue))
    )
  )
}

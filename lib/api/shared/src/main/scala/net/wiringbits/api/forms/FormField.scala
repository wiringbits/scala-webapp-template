package net.wiringbits.api.forms

import net.wiringbits.common.core.ValidationResult

class FormField[T](
    val label: String,
    val name: String = "",
    val required: Boolean = false,
    val `type`: String = "text",
    val value: Option[ValidationResult[T]] = None // by default, the field is pristine
) {

  // NOTE: This doesn't use a case class copy method on purpose because it allows changing the
  // type T to something else, which can easily cause bugs when writing the forms view
  def updated(result: ValidationResult[T]): FormField[T] = new FormField[T](
    label = label,
    name = name,
    required = required,
    `type` = `type`,
    value = Some(result)
  )

  def errorMsg: Option[String] = {
    (required, value) match {
      case (true, None) => Some("Required")
      case (_, x) => x.flatMap(_.errorMessage)
    }
  }

  def valueOpt: Option[T] = value.flatMap(_.toOption)

  def isValid: Boolean = errorMsg.isEmpty
}

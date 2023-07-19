package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult
import sttp.tapir.generic.auto.*
import sttp.tapir.{Schema, SchemaType}

class Password private (val string: String) extends WrappedString

object Password extends WrappedString.Companion[Password] {
  private val minPasswordLength: Int = 8

  override def validate(string: String): ValidationResult[Password] = {
    val isValid = string.length >= minPasswordLength

    Option
      .when(isValid)(ValidationResult.Valid(string, new Password(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid password")
      }
  }

  override def trusted(string: String): Password = new Password(string)

  implicit val passwordSchema: Schema[Password] = Schema(SchemaType.SString())
}

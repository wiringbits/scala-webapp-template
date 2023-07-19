package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult
import sttp.tapir.{Schema, SchemaType}
import sttp.tapir.generic.auto.*

class Email private (val string: String) extends WrappedString

object Email extends WrappedString.Companion[Email] {

  private val emailRegex =
    """^[\w.!#$%&'*+/=?^_`{|}~-]+@([\w-]+\.)+[\w-]{2,7}$""".r

  override def validate(string: String): ValidationResult[Email] = {
    val valid = emailRegex.findAllMatchIn(string).length == 1
    Option
      .when(valid)(ValidationResult.Valid(string, new Email(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid email")
      }
  }

  override def trusted(string: String): Email = new Email(string)

  implicit val schema: Schema[Email] = Schema(SchemaType.SString())
}

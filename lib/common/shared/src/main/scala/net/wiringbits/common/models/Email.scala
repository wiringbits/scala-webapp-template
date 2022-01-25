package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

class Email private (val string: String) extends WrappedString

object Email extends WrappedString.Companion[Email] {

  private val emailRegex =
    """^[\w.!#$%&'*+/=?^_`{|}~-]+@([\w-]+\.)+[\w-]{2,4}$""".r

  override def validate(string: String): ValidationResult[Email] = {
    val valid = emailRegex.findAllMatchIn(string).length == 1
    Option
      .when(valid)(ValidationResult.Valid(string, new Email(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid email")
      }
  }

  override def trusted(string: String): Email = new Email(string)
}

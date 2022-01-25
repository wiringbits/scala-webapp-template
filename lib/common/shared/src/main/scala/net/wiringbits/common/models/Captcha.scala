package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

class Captcha private (val string: String) extends WrappedString

object Captcha extends WrappedString.Companion[Captcha] {

  override def validate(string: String): ValidationResult[Captcha] = {

    Option(string.trim)
      .filter(_.nonEmpty)
      .map(ValidationResult.Valid(_, new Captcha(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid recaptcha")
      }
  }

  override def trusted(string: String): Captcha = new Captcha(string)
}

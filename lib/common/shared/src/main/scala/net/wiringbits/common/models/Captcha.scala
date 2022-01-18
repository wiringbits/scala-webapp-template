package net.wiringbits.common.models

import net.wiringbits.common.core.ValidationResult
import net.wiringbits.common.models.core.WrappedString

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

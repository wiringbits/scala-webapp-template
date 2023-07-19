package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult
import sttp.tapir.generic.auto.*
import sttp.tapir.{Schema, SchemaType}

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

  implicit val captchaSchema: Schema[Captcha] = Schema(SchemaType.SString())
}

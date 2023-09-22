package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

import scala.language.implicitConversions

class Name private (val string: String) extends WrappedString

object Name extends WrappedString.Companion[Name] {

  private val minNameLength: Int = 2 // we do have people named like `Jo`

  override def validate(string: String): ValidationResult[Name] = {
    val isValid = string.length >= minNameLength

    Option
      .when(isValid)(ValidationResult.Valid(string, new Name(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid name")
      }
  }

  implicit override def trusted(string: String): Name = new Name(string)
}

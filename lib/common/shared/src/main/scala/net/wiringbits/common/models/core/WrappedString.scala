package net.wiringbits.common.models.core

import net.wiringbits.common.core.{TextValidator, ValidationResult}
import play.api.libs.json._

trait WrappedString extends Any {
  def string: String

  override def toString: String = string

  override def equals(obj: Any): Boolean = obj match {
    case that: WrappedString => that.string == string
    case _ => false
  }

  override def hashCode(): Int = string.hashCode
}

object WrappedString {

  trait Companion[T <: WrappedString] {

    def trusted(string: String): T

    def validate(string: String): ValidationResult[T]

    def validateOpt(string: String): ValidationResult[Option[T]] = {
      if (string.isEmpty) ValidationResult.Valid(string, None)
      else validate(string).map(Option.apply)
    }

    implicit val validator: TextValidator[T] = validate
    implicit val format: Format[T] = deriveFormat(validate)
  }

  def deriveFormat[T <: WrappedString](validate: String => ValidationResult[T]): Format[T] = {
    Format(
      implicitly[Reads[String]].flatMapResult { string =>
        validate(string) match {
          case ValidationResult.Valid(_, value) => JsSuccess(value)
          case ValidationResult.Invalid(_, error) => JsError(error)
        }
      },
      implicitly[Writes[String]].contramap(_.string)
    )
  }
}

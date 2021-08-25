package net.wiringbits.common.core

trait TextValidator[T] {
  def apply(input: String): ValidationResult[T]
}

object TextValidator {

  implicit def optionValidator[T: TextValidator]: TextValidator[Option[T]] = input => {
    Option
      .when(input.nonEmpty)(implicitly[TextValidator[T]].apply(input).map(Option.apply))
      .getOrElse(ValidationResult.Valid(input, None))
  }
}

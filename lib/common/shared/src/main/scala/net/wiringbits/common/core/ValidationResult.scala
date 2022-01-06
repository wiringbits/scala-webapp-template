package net.wiringbits.common.core

sealed trait ValidationResult[T] extends Product with Serializable {

  def input: String

  def isValid: Boolean = this match {
    case ValidationResult.Valid(_, _) => true
    case _ => false
  }

  def errorMessage: Option[String] = this match {
    case ValidationResult.Invalid(_, error) => Some(error)
    case _ => None
  }

  def hasError: Boolean = errorMessage.isDefined

  def contains(target: T): Boolean = this match {
    case ValidationResult.Valid(_, value) => value == target
    case ValidationResult.Invalid(_, _) => false
  }

  def toOption: Option[T] = this match {
    case ValidationResult.Valid(_, value) => Option(value)
    case ValidationResult.Invalid(_, _) => None
  }

  def map[U](f: T => U): ValidationResult[U] = this match {
    case ValidationResult.Valid(_, value) => ValidationResult.Valid(input, f(value))
    case ValidationResult.Invalid(_, error) => ValidationResult.Invalid(input, error)
  }

  def flatMap[U](f: T => ValidationResult[U]): ValidationResult[U] = this match {
    case ValidationResult.Valid(_, value) => f(value)
    case ValidationResult.Invalid(_, error) => ValidationResult.Invalid(input, error)
  }
}

object ValidationResult {
  final case class Valid[T](input: String, value: T) extends ValidationResult[T]
  final case class Invalid[T](input: String, error: String) extends ValidationResult[T]
}

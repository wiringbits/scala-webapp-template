package net.wiringbits.apis.models

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait TokenType extends EnumEntry with Uppercase

object TokenType extends Enum[TokenType] {
  final case object VerificationToken extends TokenType

  val values = findValues
}

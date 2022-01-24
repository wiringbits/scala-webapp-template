package net.wiringbits.repositories.models

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait UserTokenType extends EnumEntry with Uppercase

object UserTokenType extends Enum[UserTokenType] {
  final case object EmailVerification extends UserTokenType
  final case object ResetPassword extends UserTokenType

  val values = findValues
}

package net.wiringbits.common.models.enums

import enumeratum.EnumEntry
import enumeratum.EnumEntry.Uppercase

sealed trait UserTokenType extends EnumEntry with Uppercase

object UserTokenType extends Enum[UserTokenType] {
  case object EmailVerification extends UserTokenType
  case object ResetPassword extends UserTokenType

  val values: IndexedSeq[UserTokenType] = findValues
}

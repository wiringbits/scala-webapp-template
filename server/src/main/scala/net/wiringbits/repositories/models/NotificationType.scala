package net.wiringbits.repositories.models

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait NotificationType extends EnumEntry with Uppercase

object NotificationType extends Enum[NotificationType] {
  final case object EmailVerified extends NotificationType
  final case object PasswordReset extends NotificationType
  final case object PasswordUpdated extends NotificationType

  val values = findValues
}

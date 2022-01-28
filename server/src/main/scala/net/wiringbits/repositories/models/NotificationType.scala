package net.wiringbits.repositories.models

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait NotificationType extends EnumEntry with Uppercase

object NotificationType extends Enum[NotificationType] {
  final case object EmailRegistration extends NotificationType
  final case object VerifyAccount extends NotificationType
  final case object ForgotPassword extends NotificationType
  final case object ResetPassword extends NotificationType
  final case object UpdatePassword extends NotificationType

  val values = findValues
}

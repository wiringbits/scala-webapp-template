package net.wiringbits.repositories.models

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait NotificationStatus extends EnumEntry with Uppercase

object NotificationStatus extends Enum[NotificationStatus] {
  final case object Success extends NotificationStatus
  final case object Pending extends NotificationStatus
  final case object Failed extends NotificationStatus

  val values = findValues
}

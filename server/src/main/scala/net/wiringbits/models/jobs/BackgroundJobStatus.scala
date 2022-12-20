package net.wiringbits.models.jobs

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait BackgroundJobStatus extends EnumEntry with Uppercase

object BackgroundJobStatus extends Enum[BackgroundJobStatus] {
  final case object Success extends BackgroundJobStatus
  final case object Pending extends BackgroundJobStatus
  final case object Failed extends BackgroundJobStatus

  val values = findValues
}

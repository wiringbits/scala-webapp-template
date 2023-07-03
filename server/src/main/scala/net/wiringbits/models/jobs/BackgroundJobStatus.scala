package net.wiringbits.models.jobs

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait BackgroundJobStatus extends EnumEntry with Uppercase

object BackgroundJobStatus extends Enum[BackgroundJobStatus] {
  case object Success extends BackgroundJobStatus
  case object Pending extends BackgroundJobStatus
  case object Failed extends BackgroundJobStatus

  val values = findValues
}

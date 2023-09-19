package net.wiringbits.common.models.enums

import enumeratum.EnumEntry
import enumeratum.EnumEntry.Uppercase

sealed trait BackgroundJobStatus extends EnumEntry with Uppercase

object BackgroundJobStatus extends Enum[BackgroundJobStatus] {
  case object Success extends BackgroundJobStatus
  case object Pending extends BackgroundJobStatus
  case object Failed extends BackgroundJobStatus

  val values: IndexedSeq[BackgroundJobStatus] = findValues
}

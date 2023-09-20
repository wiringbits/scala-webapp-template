package net.wiringbits.common.models.enums

import enumeratum.EnumEntry
import enumeratum.EnumEntry.Uppercase

sealed trait BackgroundJobType extends EnumEntry with Uppercase

/** NOTE: Updating this model can cause tasks to fail, for example, if SendEmail is removed while there are pending
  * SendEmail tasks stored at the database
  */
object BackgroundJobType extends Enum[BackgroundJobType] {
  case object SendEmail extends BackgroundJobType

  val values: IndexedSeq[BackgroundJobType] = findValues
}

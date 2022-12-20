package net.wiringbits.models.jobs

import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

sealed trait BackgroundJobType extends EnumEntry with Uppercase

/** NOTE: Updating this model can cause tasks to fail, for example, if SendEmail is removed while there are pending
  * SendEmail tasks stored at the database
  */
object BackgroundJobType extends Enum[BackgroundJobType] {
  final case object SendEmail extends BackgroundJobType

  val values = findValues
}

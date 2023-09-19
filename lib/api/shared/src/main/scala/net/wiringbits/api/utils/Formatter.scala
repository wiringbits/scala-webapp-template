package net.wiringbits.api.utils

import net.wiringbits.common.models.InstantCustom

import java.time.Instant

object Formatter {

  def instant(item: InstantCustom): String = {
    try {
      java.time.ZonedDateTime
        .ofInstant(item.value, java.time.ZoneId.systemDefault())
        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MMM/uuuu hh:mm a"))
    } catch {
      // if for any reason the locale is not available in the sjs libraries, the operation will fail
      // this shouldn't happen in the jvm
      case _: Throwable => item.toString
    }
  }
}

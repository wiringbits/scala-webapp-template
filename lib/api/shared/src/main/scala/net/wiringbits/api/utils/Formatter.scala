package net.wiringbits.api.utils

import java.time.Instant

object Formatter {

  def instant(item: Instant): String = {
    try {
      java.time.ZonedDateTime
        .ofInstant(item, java.time.ZoneId.systemDefault())
        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MMM/uuuu hh:mm a"))
    } catch {
      // if for any reason the locale is not available in the sjs libraries, the operation will fail
      // this shouldn't happen in the jvm
      case _: Throwable => item.toString
    }
  }
}

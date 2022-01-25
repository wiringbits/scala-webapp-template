package net.wiringbits.util

import java.util.UUID
import javax.inject.Inject

class TokenGenerator @Inject() () {
  def next(): UUID = UUID.randomUUID()
}

package net.wiringbits.util

import net.wiringbits.common.models.id.UserTokenId

import java.util.UUID
import javax.inject.Inject

class TokenGenerator @Inject() () {
  def next(): UserTokenId = UserTokenId.parse(UUID.randomUUID())
}

package net.wiringbits.validations

import net.wiringbits.repositories.models.UserToken

import java.time.Clock

object ValidateUserToken {
  def apply(token: UserToken)(implicit clock: Clock): Unit = {
    if (token.expiresAt.isBefore(clock.instant()))
      throw new RuntimeException("Token is expired")
    else ()
  }
}

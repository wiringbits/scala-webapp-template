package net.wiringbits.validations

import net.wiringbits.typo_generated.public.user_tokens.UserTokensRow

import java.time.{Clock, ZoneOffset}

object ValidateUserToken {
  def apply(token: UserTokensRow)(implicit clock: Clock): Unit = {
    if (token.expiresAt.isBefore(clock.instant()))
      throw new RuntimeException("Token is expired")
    else ()
  }
}

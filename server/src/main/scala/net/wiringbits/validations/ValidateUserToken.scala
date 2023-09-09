package net.wiringbits.validations

import net.wiringbits.typo_generated.public.user_tokens.UserTokensRow

import java.time.{Clock, ZoneOffset}

object ValidateUserToken {
  def apply(token: UserTokensRow)(implicit clock: Clock): Unit = {
    if (token.expiresAt.value.isBefore(clock.instant().atOffset(ZoneOffset.UTC)))
      throw new RuntimeException("Token is expired")
    else ()
  }
}

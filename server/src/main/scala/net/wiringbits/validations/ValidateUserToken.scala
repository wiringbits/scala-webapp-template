package net.wiringbits.validations

import net.wiringbits.common.models.InstantCustom
import net.wiringbits.typo_generated.public.user_tokens.UserTokensRow

import java.time.Clock

object ValidateUserToken {
  def apply(token: UserTokensRow)(implicit clock: Clock): Unit = {
    if (token.expiresAt.isBefore(InstantCustom.fromClock))
      throw new RuntimeException("Token is expired")
    else ()
  }
}

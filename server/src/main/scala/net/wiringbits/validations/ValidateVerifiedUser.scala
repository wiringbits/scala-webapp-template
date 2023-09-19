package net.wiringbits.validations

import net.wiringbits.common.ErrorMessages
import net.wiringbits.typo_generated.public.users.UsersRow

object ValidateVerifiedUser {
  def apply(user: UsersRow): Unit = {
    if (user.verifiedOn.isDefined)
      ()
    else
      throw new RuntimeException(ErrorMessages.emailNotVerified)
  }
}

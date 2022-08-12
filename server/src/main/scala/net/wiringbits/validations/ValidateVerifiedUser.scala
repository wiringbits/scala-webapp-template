package net.wiringbits.validations

import net.wiringbits.common.ErrorMessages
import net.wiringbits.repositories.models.User

object ValidateVerifiedUser {
  def apply(user: User): Unit = {
    if (user.verifiedOn.isDefined)
      ()
    else
      throw new RuntimeException(ErrorMessages.emailNotVerified)
  }
}

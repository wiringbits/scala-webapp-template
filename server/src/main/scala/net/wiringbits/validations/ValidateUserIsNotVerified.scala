package net.wiringbits.validations

import net.wiringbits.repositories.models.User

object ValidateUserIsNotVerified {
  def apply(user: User): Unit = {
    if (user.verifiedOn.isDefined)
      throw new RuntimeException(s"User email is already verified")
    else ()
  }
}

package net.wiringbits.validations

import net.wiringbits.repositories.models.User

object ValidateVerifiedUser {
  def apply(user: User): Unit = {
    if (user.verifiedOn.isDefined)
      ()
    else
      throw new RuntimeException("The email is not verified, check your spam folder if you don't see the email.")
  }
}

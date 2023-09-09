package net.wiringbits.validations

import net.wiringbits.typo_generated.public.users.UsersRow

object ValidateUserIsNotVerified {
  def apply(user: UsersRow): Unit = {
    if (user.verifiedOn.isDefined)
      throw new RuntimeException(s"User email is already verified")
    else ()
  }
}

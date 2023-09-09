package net.wiringbits.validations

import org.foo.generated.public.users.UsersRow

object ValidateUserIsNotVerified {
  def apply(user: UsersRow): Unit = {
    if (user.verifiedOn.isDefined)
      throw new RuntimeException(s"User email is already verified")
    else ()
  }
}

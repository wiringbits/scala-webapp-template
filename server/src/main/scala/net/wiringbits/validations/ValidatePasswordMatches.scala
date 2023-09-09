package net.wiringbits.validations

import net.wiringbits.common.models.Password
import net.wiringbits.typo_generated.public.users.UsersRow
import org.mindrot.jbcrypt.BCrypt

object ValidatePasswordMatches {
  def apply(maybe: Option[UsersRow], password: Password): UsersRow = {
    maybe
      .filter(user => BCrypt.checkpw(password.string, user.password))
      .getOrElse(throw new RuntimeException("The given email/password doesn't match"))
  }
}

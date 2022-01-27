package net.wiringbits.validations

import net.wiringbits.common.models.Password
import net.wiringbits.repositories.models.User
import org.mindrot.jbcrypt.BCrypt

object ValidatePasswordMatches {
  def apply(maybe: Option[User], password: Password): User = {
    maybe
      .filter(user => BCrypt.checkpw(password.string, user.hashedPassword))
      .getOrElse(throw new RuntimeException("The given email/password doesn't match"))
  }
}

package net.wiringbits.validations

import net.wiringbits.common.models.Password
import net.wiringbits.repositories.models.User
import net.wiringbits.services.PasswordHasher

object ValidatePasswordMatches {
  def apply(maybe: Option[User], password: Password, passwordHasher: PasswordHasher): User = {
    maybe
      .filter(user => passwordHasher.check(password.string, user.hashedPassword))
      .getOrElse(throw new RuntimeException("The given email/password doesn't match"))
  }
}

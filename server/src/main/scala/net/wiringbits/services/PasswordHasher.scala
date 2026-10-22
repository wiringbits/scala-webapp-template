package net.wiringbits.services

import org.mindrot.jbcrypt.BCrypt

trait PasswordHasher {
  def hash(password: String): String
  def check(password: String, hashedPassword: String): Boolean
}

/** bcrypt-based hasher. `logRounds` controls the cost: production uses the default 10, while tests bind a lower value
  * so the real algorithm runs with cheap parameters instead of a fake hash.
  */
class BcryptPasswordHasher(logRounds: Int) extends PasswordHasher {
  override def hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(logRounds))
  override def check(password: String, hashedPassword: String): Boolean = BCrypt.checkpw(password, hashedPassword)
}

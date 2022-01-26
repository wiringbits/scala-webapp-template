package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class UserTokensConfig(
    emailVerificationExp: FiniteDuration,
    resetPasswordExp: FiniteDuration,
    hmacSecret: String
) {
  override def toString: String = {
    import net.wiringbits.util.StringUtils.Implicits._

    s"UserTokensConfig(emailVerificationExp = $emailVerificationExp, resetPasswordExp = $resetPasswordExp, hmacSecret = ${hmacSecret.mask()})"
  }
}

object UserTokensConfig {

  def apply(conf: Configuration): UserTokensConfig = {
    val emailVerificationExp = conf.get[FiniteDuration]("emailVerification.expirationTime")
    val resetPasswordExp = conf.get[FiniteDuration]("resetPassword.expirationTime")
    val hmacSecret = conf.get[String]("hmacSecret")

    UserTokensConfig(emailVerificationExp, resetPasswordExp, hmacSecret)
  }
}

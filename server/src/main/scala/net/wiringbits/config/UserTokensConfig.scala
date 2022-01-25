package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class UserTokensConfig(emailVerificationExp: FiniteDuration, resetPasswordExp: FiniteDuration, hmacSecret: String)

object UserTokensConfig {

  def apply(conf: Configuration): UserTokensConfig = {
    val emailVerificationExp = conf.get[FiniteDuration]("emailVerification.expirationTime")
    val resetPasswordExp = conf.get[FiniteDuration]("resetPassword.expirationTime")
    val hmacSecret = conf.get[String]("hmacSecret")

    UserTokensConfig(emailVerificationExp, resetPasswordExp, hmacSecret)
  }
}

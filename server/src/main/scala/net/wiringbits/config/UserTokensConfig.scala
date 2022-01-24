package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class UserTokensConfig(emailVerificationExp: FiniteDuration, resetPasswordExp: FiniteDuration)

object UserTokensConfig {

  def apply(conf: Configuration): UserTokensConfig = {
    val emailVerificationExp = conf.get[FiniteDuration]("emailVerification.expirationTime")
    val resetPasswordExp = conf.get[FiniteDuration]("resetPassword.expirationTime")

    UserTokensConfig(emailVerificationExp, resetPasswordExp)
  }
}

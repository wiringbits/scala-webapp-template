package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class TokensConfig(verificationTokenExp: FiniteDuration)

object TokensConfig {
  def apply(config: Configuration): TokensConfig = {
    val verificationExpiration = config.get[FiniteDuration]("emailVerification.expirationTime")

    TokensConfig(verificationExpiration)
  }
}

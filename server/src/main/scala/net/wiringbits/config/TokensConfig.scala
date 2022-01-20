package net.wiringbits.config

import play.api.Configuration

import scala.concurrent.duration.FiniteDuration

case class TokensConfig(verificationTokenExp: FiniteDuration)

object TokensConfig {

  def apply(conf: Configuration): TokensConfig = {
    val verificationTokenExpiration = conf.get[FiniteDuration]("verification.expirationTime")

    TokensConfig(verificationTokenExpiration)
  }
}

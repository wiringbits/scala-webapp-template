package net.wiringbits.config

import play.api.Configuration

case class JwtConfig(secret: String)

object JwtConfig {

  def apply(config: Configuration): JwtConfig = {
    val secret = config.get[String]("secret")
    JwtConfig(secret)
  }
}

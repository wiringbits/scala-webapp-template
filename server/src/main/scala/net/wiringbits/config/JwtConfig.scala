package net.wiringbits.config

import net.wiringbits.models.JwtSecret
import play.api.Configuration

case class JwtConfig(secret: JwtSecret)

object JwtConfig {

  def apply(config: Configuration): JwtConfig = {
    val secret = config.get[String]("secret")
    JwtConfig(JwtSecret(secret))
  }
}

package net.wiringbits.config

import net.wiringbits.models.JwtSecret
import play.api.Configuration

case class JwtConfig(secret: JwtSecret) {
  override def toString: String = {

    s"JwtConfig(secret = ${secret.toString})"
  }
}

object JwtConfig {

  def apply(config: Configuration): JwtConfig = {
    val secret = config.get[String]("secret")
    JwtConfig(JwtSecret(secret))
  }
}

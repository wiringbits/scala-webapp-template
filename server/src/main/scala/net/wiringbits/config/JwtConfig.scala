package net.wiringbits.config

import net.wiringbits.models.JwtSecret
import play.api.Configuration

case class JwtConfig(secret: JwtSecret, enforced: Boolean) {
  override def toString: String = {
    s"JwtConfig(secret = ${secret.toString}, enforced = $enforced (must be false in production))"
  }
}

object JwtConfig {

  def apply(config: Configuration): JwtConfig = {
    val secret = config.get[String]("secret")
    val enforced = config.get[Boolean]("enforced")
    JwtConfig(JwtSecret(secret), enforced)
  }
}

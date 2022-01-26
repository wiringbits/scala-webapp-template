package net.wiringbits.config

import play.api.Configuration

case class JwtConfig(secret: String) {
  override def toString: String = {
    import net.wiringbits.util.StringUtils.Implicits._

    s"JwtConfig(secret = ${secret.mask()})"
  }
}

object JwtConfig {

  def apply(config: Configuration): JwtConfig = {
    val secret = config.get[String]("secret")
    JwtConfig(secret)
  }
}

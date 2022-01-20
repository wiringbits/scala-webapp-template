package net.wiringbits.config

import net.wiringbits.models.SecretAccessKey
import play.api.Configuration

case class ReCaptchaConfig(secret: SecretAccessKey)

object ReCaptchaConfig {
  def apply(config: Configuration): ReCaptchaConfig = {
    val secret = config.get[String]("secretKey")
    ReCaptchaConfig(SecretAccessKey(secret))
  }
}

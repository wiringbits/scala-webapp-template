package net.wiringbits.config

import net.wiringbits.models.ReCaptchaSecret
import play.api.Configuration

case class ReCaptchaConfig(secret: ReCaptchaSecret)

object ReCaptchaConfig {
  def apply(config: Configuration): ReCaptchaConfig = {
    val secret = config.get[String]("secretKey")
    ReCaptchaConfig(ReCaptchaSecret(secret))
  }
}

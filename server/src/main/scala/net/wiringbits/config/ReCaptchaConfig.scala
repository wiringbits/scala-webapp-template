package net.wiringbits.config

import play.api.Configuration

case class ReCaptchaConfig(secret: String)

object ReCaptchaConfig {
  def apply(config: Configuration): ReCaptchaConfig = {
    val secret = config.get[String]("secretKey")
    ReCaptchaConfig(secret)
  }
}

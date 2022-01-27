package net.wiringbits.config

import net.wiringbits.models.ReCaptchaSecret
import play.api.Configuration

case class ReCaptchaConfig(secret: ReCaptchaSecret) {
  override def toString: String = {

    s"ReCaptchaConfig(secret = ${secret.toString})"
  }
}

object ReCaptchaConfig {
  def apply(config: Configuration): ReCaptchaConfig = {
    val secret = config.get[String]("secretKey")
    ReCaptchaConfig(ReCaptchaSecret(secret))
  }
}

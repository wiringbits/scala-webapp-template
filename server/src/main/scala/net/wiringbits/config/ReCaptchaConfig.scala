package net.wiringbits.config

import net.wiringbits.models.{ReCaptchaSecret, ReCaptchaSiteKey}
import play.api.Configuration

case class ReCaptchaConfig(secret: ReCaptchaSecret, siteKey: ReCaptchaSiteKey) {
  override def toString: String = {

    s"ReCaptchaConfig(secret = ${secret.toString}, siteKey = ${siteKey})"
  }
}

object ReCaptchaConfig {
  def apply(config: Configuration): ReCaptchaConfig = {
    val secret = config.get[String]("secretKey")
    val siteKey = config.get[String]("siteKey")
    ReCaptchaConfig(ReCaptchaSecret(secret), ReCaptchaSiteKey(siteKey))
  }
}

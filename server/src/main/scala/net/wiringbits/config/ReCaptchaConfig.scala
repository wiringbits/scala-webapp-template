package net.wiringbits.config

import play.api.Configuration

case class ReCaptchaConfig(secret: String) {
  override def toString: String = {
    import net.wiringbits.util.StringUtils.Implicits._

    s"ReCaptchaConfig(secret = ${secret.mask()})"
  }
}

object ReCaptchaConfig {
  def apply(config: Configuration): ReCaptchaConfig = {
    val secret = config.get[String]("secretKey")
    ReCaptchaConfig(secret)
  }
}

package net.wiringbits.models

import com.typesafe.config.Config
import play.api.ConfigLoader

case class ReCaptchaSecret(string: String) extends SecretValue(string)

object ReCaptchaSecret {

  implicit val configLoader: ConfigLoader[SecretAccessKey] = (config: Config, path: String) => {
    SecretAccessKey(string = config.getString(path))
  }
}

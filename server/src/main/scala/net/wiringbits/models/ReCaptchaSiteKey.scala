package net.wiringbits.models

import com.typesafe.config.Config
import play.api.ConfigLoader

case class ReCaptchaSiteKey(string: String)

object ReCaptchaSiteKey {
  implicit val configLoader: ConfigLoader[ReCaptchaSiteKey] = (config: Config, path: String) => {
    ReCaptchaSiteKey(string = config.getString(path))
  }
}

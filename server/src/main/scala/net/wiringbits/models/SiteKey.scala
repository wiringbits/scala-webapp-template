package net.wiringbits.models

import com.typesafe.config.Config
import play.api.ConfigLoader

case class SiteKey(string: String)

object SiteKey {
  implicit val configLoader: ConfigLoader[SiteKey] = (config: Config, path: String) => {
    SiteKey(string = config.getString(path))
  }
}

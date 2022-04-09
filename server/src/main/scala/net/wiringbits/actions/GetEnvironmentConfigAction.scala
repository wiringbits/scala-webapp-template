package net.wiringbits.actions

import net.wiringbits.api.models.GetEnvironmentConfig
import net.wiringbits.config.ReCaptchaConfig

import javax.inject.Inject
import scala.concurrent.Future

class GetEnvironmentConfigAction @Inject() (
    reCaptchaConfig: ReCaptchaConfig
)() {
  def apply(): Future[GetEnvironmentConfig.Response] = Future.successful {
    GetEnvironmentConfig.Response(reCaptchaConfig.siteKey.string)
  }
}

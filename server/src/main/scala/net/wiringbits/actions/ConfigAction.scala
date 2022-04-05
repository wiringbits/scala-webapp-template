package net.wiringbits.actions

import net.wiringbits.api.models.GetEnvironmentConfig
import net.wiringbits.config.ReCaptchaConfig

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfigAction @Inject() (
    reCaptchaConfig: ReCaptchaConfig
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {
  def apply(): Future[GetEnvironmentConfig.Response] = Future.successful {
    GetEnvironmentConfig.Response(reCaptchaConfig.siteKey.string)
  }
}

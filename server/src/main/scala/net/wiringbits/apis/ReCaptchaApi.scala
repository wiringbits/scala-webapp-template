package net.wiringbits.apis

import net.wiringbits.common.models.Captcha
import net.wiringbits.config.ReCaptchaConfig
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReCaptchaApi @Inject() (reCaptchaConfig: ReCaptchaConfig, ws: WSClient)(implicit
    ec: ExecutionContext
) {

  private val url = "https://www.google.com/recaptcha/api/siteverify"

  def verify(captcha: Captcha): Future[Boolean] = {
    ws.url(url)
      .addQueryStringParameters("secret" -> reCaptchaConfig.secret.string, "response" -> captcha.string)
      .post("{}")
      .map { response =>
        (response.json \ "success")
          .as[Boolean]
      }
  }
}

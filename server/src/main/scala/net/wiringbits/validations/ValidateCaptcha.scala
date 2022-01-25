package net.wiringbits.validations

import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.common.models.Captcha

import scala.concurrent.{ExecutionContext, Future}

object ValidateCaptcha {
  def apply(captchaApi: ReCaptchaApi, captcha: Captcha)(implicit ec: ExecutionContext): Future[Unit] = {
    captchaApi
      .verify(captcha)
      .map {
        case true => ()
        case false => throw new RuntimeException(s"Invalid captcha, try again")
      }
  }
}

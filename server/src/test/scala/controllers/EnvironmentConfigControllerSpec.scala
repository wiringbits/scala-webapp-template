package controllers

import controllers.common.PlayPostgresSpec
import net.wiringbits.config.ReCaptchaConfig

class EnvironmentConfigControllerSpec extends PlayPostgresSpec {
  def reCaptchaConfig: ReCaptchaConfig = app.injector.instanceOf(classOf[ReCaptchaConfig])

  "GET /environment-config" should {
    "return the frontend configuration" in withApiClient { client =>
      val response = client.getEnvironmentConfig().futureValue
      response.recaptchaSiteKey must be(reCaptchaConfig.siteKey.string)
    }
  }
}

package net.wiringbits.apis

import net.wiringbits.common.models.Captcha
import net.wiringbits.config.ReCaptchaConfig
import net.wiringbits.models.{ReCaptchaSecret, ReCaptchaSiteKey}
import org.mockito.ArgumentMatchers
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{BodyWritable, WSClient, WSRequest, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReCaptchaApiSpec extends AnyWordSpec {
  private val ws = mock[WSClient]
  private val request = mock[WSRequest]
  private val response = mock[WSResponse]
  private val config = ReCaptchaConfig(ReCaptchaSecret("test"), ReCaptchaSiteKey("test"))
  private val api = new ReCaptchaApi(config, ws)

  "verify" should {
    "detect successful responses" in {
      mockRequest(request, response)(Json.obj("success" -> true))
      val result = api.verify(Captcha.trusted("example"))
      result.futureValue must be(true)
    }

    "detect unsuccessful responses" in {
      mockRequest(request, response)(Json.obj("success" -> false))
      val result = api.verify(Captcha.trusted("example"))
      result.futureValue must be(false)
    }

    "fail when getting an unknown response" in {
      mockRequest(request, response)(Json.obj("other" -> false))
      val result = api.verify(Captcha.trusted("example"))
      intercept[Throwable](result.futureValue)
    }
  }

  private def mockRequest(request: WSRequest, response: WSResponse)(body: JsValue): Unit = {
    when(ws.url(ArgumentMatchers.anyString)).thenReturn(request)
    when(request.addQueryStringParameters(ArgumentMatchers.any[(String, String)])).thenReturn(request)
    when(response.json).thenReturn(body)
    val _ =
      when(request.post[String](ArgumentMatchers.anyString())(ArgumentMatchers.eq(implicitly[BodyWritable[String]])))
        .thenReturn(Future.successful(response))
  }
}

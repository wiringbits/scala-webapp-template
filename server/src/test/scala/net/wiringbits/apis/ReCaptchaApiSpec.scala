package net.wiringbits.apis

import net.wiringbits.common.models.Captcha
import net.wiringbits.config.ReCaptchaConfig
import net.wiringbits.models.{ReCaptchaSecret, ReCaptchaSiteKey}
//import org.mockito.ArgumentMatchers
//import org.mockito.MockitoSugar.{mock, when}

import org.scalatest.matchers.should.Matchers

//import eu.monniot.scala3mock.macros.{mock, when}
//import eu.monniot.scala3mock.main.withExpectations
//import eu.monniot.scala3mock.scalatest.MockFactory
//import eu.monniot.scala3mock.functions.MockFunctions.mockFunction
//import eu.monniot.scala3mock.matchers.ArgumentMatcher
//import eu.monniot.scala3mock.matchers.MatchAny

import org.scalatest.OneInstancePerTest
import org.scalatest.concurrent.ScalaFutures._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.flatspec.AnyFlatSpec

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{BodyWritable, WSClient, WSRequest, WSResponse}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


import org.scalatest.matchers.should.Matchers._
import org.scalatest._

import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.mockito.MockitoSugar

class ReCaptchaApiSpec extends AnyFlatSpec{


   val ws = mock[WSClient]
   val request = mock[WSRequest]
   val response = mock[WSResponse]
   val config = ReCaptchaConfig(ReCaptchaSecret("test"), ReCaptchaSiteKey("test"))
   val api = new ReCaptchaApi(config, ws)


   private def mockRequest(request: WSRequest, response: WSResponse)(body: JsValue): Unit = {
   when(()=>ws.url(mock[String])).expects().returns(request)
   when(()=>request.addQueryStringParameters(mock[(String, String)])).expects().returns(request)
   when(()=>response.json).expects().returns(body)  }




   "verify" should "detect successful responses" in  {
      mockRequest(request, response)(Json.obj("success" -> true))
      val result = api.verify(Captcha.trusted("example"))
      result.futureValue must be(true)
    }

    "verify" should "detect unsuccessful responses" in {
      mockRequest(request, response)(Json.obj("success" -> false))
      val result = api.verify(Captcha.trusted("example"))
      result.futureValue must be(false)
    }

    "verify" should "fail when getting an unknown response" in {
      mockRequest(request, response)(Json.obj("other" -> false))
      val result = api.verify(Captcha.trusted("example"))
      intercept[Throwable](result.futureValue)
    }
  }



package controllers.common

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.slf4j.LoggerFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, Mode}

import java.net.URLEncoder
import scala.concurrent.Future

/**
 * A PlayAPISpec allow us to write tests for the API calls.
 */
trait PlayAPISpec extends PlaySpec with ScalaFutures {

  protected def defaultGuiceApplicationBuilder: GuiceApplicationBuilder =
    GuiceApplicationBuilder()
      .in(Mode.Test)

  private val JsonHeader = CONTENT_TYPE -> "application/json"
  private val EmptyJson = "{}"

  protected val logger = LoggerFactory.getLogger(this.getClass)

  def log[T](request: FakeRequest[T], response: Future[Result]): Unit = {
    logger.info(
      s"REQUEST > $request, headers = ${request.headers}; RESPONSE < status = ${status(response)}, body = ${contentAsString(response)}"
    )
  }

  /** Syntactic sugar for calling APIs * */
  def GET(url: String, extraHeaders: (String, String)*)(implicit application: Application): Future[Result] = {
    val headers = JsonHeader :: extraHeaders.toList
    val request = FakeRequest("GET", url)
      .withHeaders(headers: _*)

    val response = route(application, request).value
    log(request, response)
    response
  }

  def POST(url: String, extraHeaders: (String, String)*)(implicit application: Application): Future[Result] = {
    POST(url, EmptyJson, extraHeaders: _*)
  }

  def POST(url: String, jsonBody: String, extraHeaders: (String, String)*)(implicit
      application: Application
  ): Future[Result] = {
    val headers = JsonHeader :: extraHeaders.toList
    val request = FakeRequest("POST", url)
      .withHeaders(headers: _*)
      .withBody(jsonBody)

    val response = route(application, request).value
    log(request, response)
    response
  }

  def PUT(url: String, extraHeaders: (String, String)*)(implicit application: Application): Future[Result] = {
    PUT(url, EmptyJson, extraHeaders: _*)
  }

  def PUT(url: String, jsonBody: String, extraHeaders: (String, String)*)(implicit
      application: Application
  ): Future[Result] = {
    val headers = JsonHeader :: extraHeaders.toList
    val request = FakeRequest("PUT", url)
      .withHeaders(headers: _*)
      .withBody(jsonBody)

    val response = route(application, request).value
    log(request, response)
    response
  }

  def DELETE(url: String, extraHeaders: (String, String)*)(implicit application: Application): Future[Result] = {
    val headers = JsonHeader :: extraHeaders.toList
    val request = FakeRequest("DELETE", url)
      .withHeaders(headers: _*)

    val response = route(application, request).value
    log(request, response)

    response
  }
}

object PlayAPISpec {

  object Implicits {

    implicit class HttpExt(val params: List[(String, String)]) extends AnyVal {

      def toQueryString: String = {
        params
          .map { case (key, value) =>
            val encodedKey = URLEncoder.encode(key, "UTF-8")
            val encodedValue = URLEncoder.encode(value, "UTF-8")
            List(encodedKey, encodedValue).mkString("=")
          }
          .mkString("&")
      }
    }

    implicit class StringUrlExt(val url: String) extends AnyVal {

      def withQueryParams(params: (String, String)*): String = {
        List(url, params.toList.toQueryString).mkString("?")
      }
    }
  }
}

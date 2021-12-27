package controllers

import net.wiringbits.api.models.{ErrorResponse, PlayErrorResponse}
import org.scalatest.concurrent.ScalaFutures.*
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json, Reads}
import sttp.client3.{UriContext, asString, basicRequest}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}

class SampleSpec extends AnyWordSpec {
  implicit val patienceConfig: PatienceConfig = PatienceConfig(30.seconds, 1.second)

  private def asJson[R: Reads] = {
    asString
      .map {
        case Right(response) =>
          // handles 2xx responses
          Success(response)
        case Left(response) =>
          // handles non 2xx responses
          Try {
            val json = Json.parse(response)
            // TODO: Unify responses to match the play error format
            json
              .asOpt[ErrorResponse]
              .orElse {
                json
                  .asOpt[PlayErrorResponse]
                  .map(model => ErrorResponse(model.error.message))
              }
              .getOrElse(throw new RuntimeException(s"Unexpected JSON response: $response"))
          } match {
            case Failure(exception) =>
              println(s"Unexpected response: ${exception.getMessage}")
              exception.printStackTrace()
              Failure(new RuntimeException(s"Unexpected response, please try again in a minute"))
            case Success(value) =>
              Failure(new RuntimeException(value.error))
          }
      }
      .map { t =>
        t.map(Json.parse).map(_.as[R])
      }
  }

  import sttp.model.*
  val serverApi = uri"https://hopin.com/api/v2/events/219273/users/paginated"

  private def prepareRequest[R: Reads] = {
    basicRequest
      .contentType(MediaType.ApplicationJson)
      .header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:93.0) Gecko/20100101 Firefox/93.0")
      .header("Accept", "*/*")
      .header("Accept-Language", "en-US,en;q=0.5")
      .header("Referrer", "https://app.hopin.com/")
      .header("Origin", "https://app.hopin.com")
      .header("DNT", "1")
      .header("Connection", "keep-alive")
      .header("Sec-Fetch-Dest", "empty")
      .header("Sec-Fetch-Mode", "cors")
      .header("Sec-Fetch-Site", "same-site")
      .header("Sec-GPC", "1")
      .header("Pragma", "no-cache")
      .header("Cache-Control", "no-cache")
      .header("TE", "trailers")
      .header(
        "Authorization",
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmZjM0Nzk4Mi0wZGM4LTQyYWMtYmY0MC0yNzQ3ZmNkZDE1MjQiLCJzdWIiOjg3OTUxNzgsInJlc3RyaWN0ZWQiOnRydWUsInBlcnNvbmFfaWQiOjYxNjgyMywicmVnaXN0cmF0aW9uX2lkIjoxMjE0NzMxMiwicmVnaXN0cmF0aW9uX2V4dGVybmFsX2lkIjoiaU5mMmhRQTRBRVBHRkM5WmJnMmZDcjdMQyIsImV2ZW50X2lkIjoyMTkyNzMsImV2ZW50X2V4dGVybmFsX2lkIjoiZ05rSlVzMFJNRnFXT09FbkFlUXJ4cTRJMiIsInJvbGUiOiJhdHRlbmRlZSIsIm11bHRpcGxlX2Nvbm4iOnRydWUsImRhdGFfc2VncmVnYXRlZCI6ZmFsc2V9.DFfawwqrmPoZUC2Pr0qf-Q6GTJOaNsEo3uvz8iGTm8A"
      )
      .response(asJson[R])
  }

  // https://hopin.com/api/v2/events/219273/users/paginated?page%5Bafter%5D=XO3rUDNbAA0TsdwlsfEczdQ3y
  private def runIt(): Unit = {
    import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val sttpBackend = AsyncHttpClientFutureBackend()

    def f(cursorMaybe: Option[String]): List[JsValue] = {
      val dest = cursorMaybe
        .map { cursor =>
          serverApi
            .addQuerySegment(Uri.QuerySegment.KeyValue("page[after]", cursor))
        }
        .getOrElse(serverApi)

      val response = prepareRequest[JsValue]
        .get(dest)
        .send(sttpBackend)
        .map(_.body)
        .flatMap(Future.fromTry)
        .futureValue

      val nextCursor = (response \ "meta" \ "page" \ "cursor").asOpt[String]
      val hasMore = (response \ "meta" \ "page" \ "has_more_items").asOpt[Boolean]
      val users = (response \ "users").as[List[JsValue]]

      if (hasMore.contains(true)) {
        println(s"There are more users, got ${users.size}")
        f(nextCursor) ::: users
      } else {
        println("No more users")
        users
      }
    }

    val users = f(None)
    println(s"Total: ${users.size}")
    println(Json.prettyPrint(Json.toJson(users)))
  }

  "This test" should {
    "work" in {
      runIt()
      succeed
    }
  }
}

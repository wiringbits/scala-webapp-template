package controllers

import akka.stream.Materializer
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.model.headers.{Cookie, CookieValueWithMeta, CookieWithMeta}
import sttp.tapir.server.play.PlayServerInterpreter

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HealthController @Inject() (implicit ec: ExecutionContext, mat: Materializer) extends SimpleRouter {
  private val interpreter = PlayServerInterpreter()

  private def check: Future[Either[Unit, CookieValueWithMeta]] =
    Future.successful(
      Right(
        CookieValueWithMeta(
          value = "World",
          expires = Some(Instant.now().plus(3L, ChronoUnit.DAYS)),
          maxAge = Some(3L),
          domain = None,
          path = None,
          secure = false,
          httpOnly = false,
          sameSite = None,
          otherDirectives = Map.empty
        )
      )
    )

  override def routes: Routes = {
    interpreter.toRoutes(HealthController.check.serverLogic(_ => check))
  }
}

object HealthController {
  import sttp.tapir.*
  import sttp.tapir.json.circe.*

  private val baseEndpoint = endpoint
    .tag("Misc")
    .in("health")

  private val check = baseEndpoint.get
    .out(emptyOutput.description("The app is healthy"))
    .out(setCookie("Hello"))
    .summary("Queries the application's health")

  val routes: List[Endpoint[_, _, _, _, _]] = List(
    check
  )
}

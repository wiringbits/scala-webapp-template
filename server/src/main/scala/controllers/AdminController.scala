package controllers

import akka.http.javadsl.model.headers.WWWAuthenticate
import akka.stream.Materializer
import controllers.AdminController.getUserLogsEndpoint
import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers}
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.services.AdminService
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.play.PlayServerInterpreter

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminController @Inject() (
    adminService: AdminService
)(implicit ec: ExecutionContext, mat: Materializer)
    extends SimpleRouter {
  private val logger = LoggerFactory.getLogger(this.getClass)
  private val interpreter = PlayServerInterpreter()

  private def getUserLogs(
      userId: UUID,
      adminCookie: String,
      authBasic: String
  ): Future[Either[Unit, AdminGetUserLogs.Response]] = {
    logger.info(s"Get user logs: $userId")
    for {
      response <- adminService.userLogs(userId)
    } yield Right(response)
  }

  private def getUsers(adminCookie: String, authBasic: String): Future[Either[Unit, AdminGetUsers.Response]] = {
    logger.info(s"Get users")
    for {
      response <- adminService.users()
      // TODO: Avoid masking data when this the admin website is not public
      maskedResponse = response.copy(data = response.data.map(_.copy(email = Email.trusted("email@wiringbits.net"))))
    } yield Right(maskedResponse)
  }

  override def routes: Routes = {
    // TODO: do this in a better way, fold?
    interpreter
      .toRoutes(AdminController.getUserLogsEndpoint.serverLogic(getUserLogs))
      .orElse(interpreter.toRoutes(AdminController.getUsersEndpoint.serverLogic(getUsers)))
  }
}

object AdminController {
  import sttp.model.StatusCode
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val baseEndpoint = endpoint
    .in("admin")
    .tag("Admin")

  private val authBasic = auth
    .basic[String]()
    .securitySchemeName("Basic authorization")
    .description("Admin credentials")

  private val adminCookie = cookie[String]("X-Forwarded-User")
    .default("Unknown")
    .schema(_.hidden(true))

  private val getUserLogsEndpoint = baseEndpoint.get
    .in("users" / path[UUID]("userId") / "logs")
    .in(adminCookie)
    .in(authBasic)
    .out(
      jsonBody[AdminGetUserLogs.Response].example(
        AdminGetUserLogs.Response(
          List(
            AdminGetUserLogs.Response
              .UserLog(
                id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                message = "Message",
                createdAt = Instant.parse("2021-01-01T00:00:00Z")
              )
          )
        )
      )
    )
    .errorOut(oneOf(HttpErrors.badRequest, HttpErrors.unauthorized))
    .summary("Get the logs for a specific user")

  private val getUsersEndpoint = baseEndpoint.get
    .in("users")
    .in(adminCookie)
    .in(authBasic)
    .out(
      jsonBody[AdminGetUsers.Response].example(
        AdminGetUsers.Response(
          List(
            AdminGetUsers.Response.User(
              id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
              name = Name.trusted("Alexis"),
              email = Email.trusted("alexis@wiringbits.net"),
              createdAt = Instant.parse("2021-01-01T00:00:00Z")
            )
          )
        )
      )
    )
    .errorOut(oneOf(HttpErrors.badRequest, HttpErrors.unauthorized))
    .summary("Get the registered users")

  val routes: List[Endpoint[_, _, _, _, _]] = List(
    getUserLogsEndpoint,
    getUsersEndpoint
  )
}

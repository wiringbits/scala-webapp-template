package controllers

import akka.http.javadsl.model.headers.WWWAuthenticate
import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers}
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.services.AdminService
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import sttp.model.headers.WWWAuthenticateChallenge

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AdminController @Inject() (
    adminService: AdminService
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getUserLogs(userIdStr: String): Action[AnyContent] = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get user logs: $userIdStr")
      userId = UUID.fromString(userIdStr)
      response <- adminService.userLogs(userId)
    } yield Ok(Json.toJson(response))
  }

  def getUsers: Action[AnyContent] = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get users")
      response <- adminService.users()
      // TODO: Avoid masking data when this the admin website is not public
      maskedResponse = response.copy(data = response.data.map(_.copy(email = Email.trusted("email@wiringbits.net"))))
    } yield Ok(Json.toJson(maskedResponse))
  }
}

object AdminController {
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val getUserLogsEndpoint = endpoint.get
    .in("admin" / "users" / path[UUID]("userId") / "logs")
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
    .summary("Get the logs for a specific user")

  private val getUsersEndpoint = endpoint.get
    .in("admin" / "users")
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
    .summary("Get the registered users")

  val routes: List[PublicEndpoint[_, _, _, _]] = List(
    getUserLogsEndpoint,
    getUsersEndpoint
  ).map(_.tag("Admin"))
}

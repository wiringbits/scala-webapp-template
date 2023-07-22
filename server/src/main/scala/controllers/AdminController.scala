package controllers

import controllers.AdminController.getUserLogsEndpoint
import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers, ErrorResponse}
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.services.AdminService
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminController @Inject() (
    adminService: AdminService
)(implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def getUserLogs(
      authBasic: String,
      userId: UUID,
      adminCookie: String
  ): Future[Either[ErrorResponse, AdminGetUserLogs.Response]] = handleRequest {
    logger.info(s"Get user logs: $userId")
    for {
      response <- adminService.userLogs(userId)
    } yield Right(response)
  }

  private def getUsers(
      adminCookie: String,
      authBasic: String
  ): Future[Either[ErrorResponse, AdminGetUsers.Response]] = handleRequest {
    logger.info(s"Get users")
    for {
      response <- adminService.users()
      // TODO: Avoid masking data when this the admin website is not public
      maskedResponse = response.copy(data = response.data.map(_.copy(email = Email.trusted("email@wiringbits.net"))))
    } yield Right(maskedResponse)
  }

  def routes: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] = {
    List(
      AdminController.getUserLogsEndpoint.serverLogic(getUserLogs),
      AdminController.getUsersEndpoint.serverLogic(getUsers)
    )
  }
}

object AdminController {
  import sttp.tapir.*
  import sttp.tapir.json.play.*

  private val baseEndpoint = endpoint
    .in("admin")
    .tag("Admin")
    .in(adminAuth)
    .errorOut(errorResponseErrorOut)

  private val getUserLogsEndpoint = baseEndpoint.get
    .in("users" / path[UUID]("userId") / "logs")
    .in(adminCookie)
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

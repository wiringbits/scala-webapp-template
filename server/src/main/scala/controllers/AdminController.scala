package controllers

import net.wiringbits.api.endpoints.AdminEndpoints
import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers, ErrorResponse}
import net.wiringbits.common.models.Email
import net.wiringbits.services.AdminService
import net.wiringbits.typo_generated.customtypes.TypoUUID
import net.wiringbits.typo_generated.public.users.UsersId
import org.slf4j.LoggerFactory
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

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
    val usersId = UsersId(TypoUUID(userId))
    for {
      response <- adminService.userLogs(usersId)
    } yield Right(response)
  }

  private def getUsers(
      authBasic: String,
      adminCookie: String
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
      AdminEndpoints.getUserLogsEndpoint.serverLogic(getUserLogs),
      AdminEndpoints.getUsersEndpoint.serverLogic(getUsers)
    )
  }
}

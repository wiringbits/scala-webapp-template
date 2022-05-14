package controllers

import io.swagger.annotations._
import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers}
import net.wiringbits.common.models.Email
import net.wiringbits.services.AdminService
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@SwaggerDefinition(
  securityDefinition = new SecurityDefinition(
    basicAuthDefinitions = Array(
      new BasicAuthDefinition(key = "admin_credentials", description = "Admin credentials")
    )
  )
)
@Api(value = "Admin", authorizations = Array(new Authorization(value = "admin_credentials")))
class AdminController @Inject() (
    adminService: AdminService
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  @ApiOperation(
    value = "Get the logs for a specific user"
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The account was created", response = classOf[AdminGetUserLogs.Response]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments"),
      new ApiResponse(code = 401, message = "Invalid or missing authentication")
    )
  )
  def getUserLogs(userIdStr: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get user logs: $userIdStr")
      userId = UUID.fromString(userIdStr)
      response <- adminService.userLogs(userId)
    } yield Ok(Json.toJson(response))
  }

  @ApiOperation(
    value = "Get the registered users"
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The account was created", response = classOf[AdminGetUsers.Response]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments"),
      new ApiResponse(code = 401, message = "Invalid or missing authentication")
    )
  )
  def getUsers() = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get users")
      response <- adminService.users()
      // TODO: Avoid masking data when this the admin website is not public
      maskedResponse = response.copy(data = response.data.map(_.copy(email = Email.trusted("email@wiringbits.net"))))
    } yield Ok(Json.toJson(maskedResponse))
  }
}

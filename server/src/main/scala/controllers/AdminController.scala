package controllers

import net.wiringbits.api.models._
import net.wiringbits.services.AdminService
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AdminController @Inject() (
    adminService: AdminService
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getUserLogs(userIdStr: String, scrollId: String, limit: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get user logs: $userIdStr")
      userId = UUID.fromString(userIdStr)
      userLogId = UUID.fromString(scrollId)
      limitInt = limit.toInt
      response <- adminService.userLogs(userId,limitInt,userLogId)
    } yield Ok(Json.toJson(response))
  }

  def getUsers() = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get users")
      response <- adminService.users()
      // TODO: Avoid masking data when this the admin website is not public
      maskedResponse = response.copy(data = response.data.map(_.copy(email = "masked_email")))
    } yield Ok(Json.toJson(maskedResponse))
  }
}

package controllers

import net.wiringbits.api.models._
import net.wiringbits.config.JwtConfig
import net.wiringbits.services.{UserLogsService, UsersService}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UsersController @Inject() (
    usersService: UsersService,
    loggerService: UserLogsService
)(implicit cc: ControllerComponents, ec: ExecutionContext, jwtConfig: JwtConfig)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def create() = handleJsonBody[CreateUserRequest] { request =>
    val body = request.body
    logger.info(s"Create user: $body")
    for {
      response <- usersService.create(body)
    } yield Ok(Json.toJson(response))
  }

  def login() = handleJsonBody[LoginRequest] { request =>
    val body = request.body
    logger.info(s"Login: ${body.email}")
    for {
      response <- usersService.login(body)
    } yield Ok(Json.toJson(response))
  }

  def update() = handleJsonBody[UpdateUserRequest] { request =>
    val body = request.body
    logger.info(s"Update user: $body")
    for {
      userId <- authenticate(request)
      _ <- usersService.update(userId, body)
      response = UpdateUserResponse()
    } yield Ok(Json.toJson(response))
  }

  def getCurrentUser() = handleGET { request =>
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Get user info: $userId")
      response <- usersService.getCurrentUser(userId)
    } yield Ok(Json.toJson(response))
  }

  def getLogs(scrollId: String,limit: String) = handleGET { request =>
      logger.info(s"limit: $limit")
    for {
      userId <- authenticate(request)
      _ = logger.info(s"Get user logs: $userId")
      userLogId = UUID.fromString(scrollId)
      limitInt = limit.toInt
      response <- loggerService.logs(userId,limitInt,userLogId)
    } yield Ok(Json.toJson(response))
  }
}

package controllers

import net.wiringbits.api.models._
import net.wiringbits.services.AdminService
import net.wiringbits.util.models.pagination.{Limit, Offset, PaginatedQuery}
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

  def getUserLogs(userIdStr: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get user logs: $userIdStr")
      userId = UUID.fromString(userIdStr)
      response <- adminService.userLogs(userId)
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

  def getTables() = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get tables from database")
      response <- adminService.tables()
    } yield Ok(Json.toJson(response))
  }

  def getTableMetadata(tableName: String, offset: Int, limit: Int) = handleGET { request =>
    val query = PaginatedQuery(Offset(offset), Limit(limit))
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get table metadata with $offset offSet and $limit limit from $tableName")
      response <- adminService.tableMetadata(tableName, query)
    } yield Ok(Json.toJson(response))
  }

  def find(tableName: String, ID: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Find $ID on $tableName")
      response <- adminService.find(tableName, ID)
    } yield Ok(Json.toJson(response))
  }

  def create(tableName: String) = handleJsonBody[AdminCreateTableRequest] { request =>
    val body = request.body
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Create on $tableName with ${body.data}")
      _ <- adminService.create(tableName, body)
      response = AdminCreateTableResponse()
    } yield Ok(Json.toJson(response))
  }

  def update(tableName: String, ID: String) = handleJsonBody[AdminUpdateTableRequest] { request =>
    val body = request.body
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Update $ID on $tableName with ${body.data}")
      _ <- adminService.update(tableName, ID, body)
      response = AdminUpdateTableResponse()
    } yield Ok(Json.toJson(response))
  }

  def delete(tableName: String, ID: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Delete on $tableName")
      _ <- adminService.delete(tableName, ID)
      response = AdminDeleteTableResponse()
    } yield Ok(Json.toJson(response))
  }
}

package net.wiringbits.services

import net.wiringbits.api.models.*
import net.wiringbits.config.models.DataExplorerSettings
import net.wiringbits.repositories.{DatabaseTablesRepository, UserLogsRepository, UsersRepository}
import net.wiringbits.util.models.pagination.PaginatedQuery

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (
    userLogsRepository: UserLogsRepository,
    usersRepository: UsersRepository,
    databaseTablesRepository: DatabaseTablesRepository,
    tableSettings: DataExplorerSettings
)(implicit
    ec: ExecutionContext
) {

  def userLogs(userId: UUID): Future[AdminGetUserLogsResponse] = {
    for {
      logs <- userLogsRepository.logs(userId)
      items = logs.map { x =>
        AdminGetUserLogsResponse.UserLog(
          id = x.userLogId,
          createdAt = x.createdAt,
          message = x.message
        )
      }
    } yield AdminGetUserLogsResponse(items)
  }

  def users(): Future[AdminGetUsersResponse] = {
    for {
      users <- usersRepository.all()
      items = users.map { x =>
        AdminGetUsersResponse.User(
          id = x.id,
          name = x.name,
          email = x.email,
          createdAt = x.createdAt
        )
      }
    } yield AdminGetUsersResponse(items)
  }

  def tables(): Future[AdminGetTablesResponse] = {
    for {
      tables <- databaseTablesRepository.getTablesInSettings(tableSettings)
      items = tables.map { x =>
        AdminGetTablesResponse.DatabaseTable(
          name = x.name
        )
      }
    } yield AdminGetTablesResponse(items)
  }

  def tableMetadata(tableName: String, pagination: PaginatedQuery): Future[AdminGetTableMetadataResponse] = {
    for {
      _ <- validate(tableName, pagination)
      tableMetadata <- databaseTablesRepository.getTableMetadata(tableName, pagination)
    } yield AdminGetTableMetadataResponse(
      name = tableMetadata.data.name,
      fields = tableMetadata.data.fields.map(x => AdminGetTableMetadataResponse.TableField(x.name, x.`type`)),
      rows = tableMetadata.data.rows.map(x =>
        AdminGetTableMetadataResponse.TableRow(x.data.map(_.value).map(AdminGetTableMetadataResponse.Cell.apply))
      ),
      offSet = tableMetadata.offset.int,
      limit = tableMetadata.limit.int,
      count = tableMetadata.total.int
    )
  }

  def find(tableName: String, ID: String): Future[AdminFindTableResponse] = {
    for {
      _ <- validateTableName(tableName)
      row <- databaseTablesRepository.find(tableName, ID)
    } yield AdminFindTableResponse(
      row = AdminGetTableMetadataResponse.TableRow(row.data.map(_.value).map(AdminGetTableMetadataResponse.Cell.apply))
    )
  }

  def create(tableName: String, request: AdminCreateTableRequest): Future[Unit] = {
    val body = request.data
    val validate = for {
      _ <- validateTableName(tableName)
      _ <- validateTableFields(tableName, body)
      obligatoryFields <- databaseTablesRepository.getObligatoryFields(tableName)
      nameOfObligatoryFields = obligatoryFields.map(_.name)
    } yield
      if (nameOfObligatoryFields.forall(request.data.contains)) ()
      else
        throw new RuntimeException(
          s"Requires: ${nameOfObligatoryFields.filterNot(request.data.contains).mkString(", ")}"
        )

    for {
      _ <- validate
      _ <- databaseTablesRepository.create(tableName, body)
    } yield ()
  }

  def update(tableName: String, ID: String, request: AdminUpdateTableRequest): Future[Unit] = {
    val validate = Future {
      if (request.data.isEmpty) throw new RuntimeException(s"You need to send some data")
      else ()
    }

    val body = request.data
    for {
      _ <- validate
      _ <- validateTableName(tableName)
      _ <- validateTableFields(tableName, body)
      _ <- databaseTablesRepository.update(tableName, ID, body)
    } yield ()
  }

  def delete(tableName: String, ID: String): Future[Unit] = {
    for {
      _ <- validateTableName(tableName)
      _ <- databaseTablesRepository.delete(tableName, ID)
    } yield ()
  }

  private def validateTableFields(tableName: String, body: Map[String, String]): Future[Unit] = {
    for {
      fields <- databaseTablesRepository.getTableFields(tableName)
      fieldsNames = fields.map(_.name)
      requestFields = body.keys
      exists = requestFields.forall(fieldsNames.contains)
    } yield if (exists) () else throw new RuntimeException(s"A field doesn't correspond to this table schema")
  }

  private def validate(tableName: String, pagination: PaginatedQuery): Future[Unit] = {
    for {
      _ <- Future {
        validatePagination(pagination)
      }
      _ <- validateTableName(tableName)
    } yield ()
  }

  private def validateTableName(tableName: String): Future[Unit] = {
    for {
      tables <- databaseTablesRepository.getTablesInSettings(tableSettings)
      exists = tables.exists(_.name == tableName)
    } yield
      if (exists) () else throw new RuntimeException(s"Unexpected error because the DB table wasn't found: $tableName")
  }

  private def validatePagination(pagination: PaginatedQuery): Unit = {
    if (0 > pagination.offset.int || 0 > pagination.limit.int) {
      throw new RuntimeException(s"You can't query a table using negative numbers as a limit or offset")
    }

  }
}

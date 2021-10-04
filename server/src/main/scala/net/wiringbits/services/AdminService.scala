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
      tables <- databaseTablesRepository.getSettingsTables(tableSettings)
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

  private def validate(tableName: String, pagination: PaginatedQuery): Future[Unit] = {
    for {
      _ <- Future {
        validatePagination(pagination)
      }
      _ <- validateTableName(tableName)
    } yield ()
  }

  private def validateTableName(tableName: String): Future[Unit] = {
    val authorizatedTables = tableSettings.tables
    for {
      tables <- databaseTablesRepository.all()
      exists = tables.exists(_.name == tableName) && authorizatedTables.exists(_.name == tableName)
    } yield
      if (exists) () else throw new RuntimeException(s"Unexpected error because the DB table wasn't found: $tableName")
  }

  private def validatePagination(pagination: PaginatedQuery): Unit = {
    if (0 > pagination.offset.int || 0 > pagination.limit.int) {
      throw new RuntimeException(s"You can't query a table using negative numbers as a limit or offset")
    }

  }
}

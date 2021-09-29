package net.wiringbits.services

import net.wiringbits.api.models.*
import net.wiringbits.repositories.{DatabaseTablesRepository, UserLogsRepository, UsersRepository}
import net.wiringbits.util.Pagination

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (
    userLogsRepository: UserLogsRepository,
    usersRepository: UsersRepository,
    databaseTablesRepository: DatabaseTablesRepository
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
      tables <- databaseTablesRepository.all()
      items = tables.map { x =>
        AdminGetTablesResponse.DatabaseTable(
          name = x.name
        )
      }
    } yield AdminGetTablesResponse(items)
  }

  def tableMetadata(tableName: String, pagination: Pagination): Future[AdminGetTableMetadataResponse] = {
    for {
      _ <- validateTableName(tableName)
      tableMetadata <- databaseTablesRepository.getTableMetadata(tableName, pagination)
    } yield AdminGetTableMetadataResponse(
      name = tableMetadata.name,
      columns = tableMetadata.columns.map(x => AdminGetTableMetadataResponse.ColumnMetadata(x.name, x.`type`)),
      rows = tableMetadata.rows.map(x =>
        AdminGetTableMetadataResponse.RowMetadata(x.row.map(_.data).map(AdminGetTableMetadataResponse.Cell.apply))
      )
    )
  }

  def validateTableName(tableName: String): Future[Unit] = {
    for {
      tables <- databaseTablesRepository.all()
    } yield {
      if (
        !tables
          .map { maybe =>
            maybe.name

          }
          .contains(tableName)
      )
        throw new RuntimeException(s"Unexpected error because the DB table wasn't found: $tableName")
    }

  }
}

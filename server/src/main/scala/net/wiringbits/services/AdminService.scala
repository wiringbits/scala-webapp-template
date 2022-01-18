package net.wiringbits.services

import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers}
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (userLogsRepository: UserLogsRepository, usersRepository: UsersRepository)(implicit
    ec: ExecutionContext
) {

  def userLogs(userId: UUID): Future[AdminGetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(userId)
      items = logs.map { x =>
        AdminGetUserLogs.Response.UserLog(
          id = x.userLogId,
          createdAt = x.createdAt,
          message = x.message
        )
      }
    } yield AdminGetUserLogs.Response(items)
  }

  def users(): Future[AdminGetUsers.Response] = {
    for {
      users <- usersRepository.all()
      items = users.map { x =>
        AdminGetUsers.Response.User(
          id = x.id,
          name = x.name,
          email = x.email,
          createdAt = x.createdAt
        )
      }
    } yield AdminGetUsers.Response(items)
  }
}

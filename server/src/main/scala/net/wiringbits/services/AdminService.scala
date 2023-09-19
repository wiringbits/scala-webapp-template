package net.wiringbits.services

import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers}
import net.wiringbits.common.models.id.UserId
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (userLogsRepository: UserLogsRepository, usersRepository: UsersRepository)(implicit
    ec: ExecutionContext
) {

  def userLogs(userId: UserId): Future[AdminGetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(userId)
      items = logs.map { x =>
        AdminGetUserLogs.Response.UserLog(
          userLogId = x.userLogId,
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
          userId = x.userId,
          name = x.name,
          email = x.email,
          createdAt = x.createdAt.value
        )
      }
    } yield AdminGetUsers.Response(items)
  }
}

package net.wiringbits.services

import net.wiringbits.api.models.{AdminGetUserLogs, AdminGetUsers}
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}
import net.wiringbits.typo_generated.public.users.UsersId

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (userLogsRepository: UserLogsRepository, usersRepository: UsersRepository)(implicit
    ec: ExecutionContext
) {

  def userLogs(usersId: UsersId): Future[AdminGetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(usersId)
      items = logs.map { x =>
        AdminGetUserLogs.Response.UserLog(
          id = x.userLogId.value.value,
          createdAt = x.createdAt.value.toInstant,
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
          id = x.userId.value.value,
          name = Name.trusted(x.name),
          email = Email.trusted(x.email.value),
          createdAt = x.createdAt.value.toInstant
        )
      }
    } yield AdminGetUsers.Response(items)
  }
}

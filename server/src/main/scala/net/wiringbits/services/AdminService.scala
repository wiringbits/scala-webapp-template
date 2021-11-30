package net.wiringbits.services

import net.wiringbits.api.models._
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (userLogsRepository: UserLogsRepository, usersRepository: UsersRepository)(implicit
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
}

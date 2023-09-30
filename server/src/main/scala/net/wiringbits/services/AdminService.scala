package net.wiringbits.services

import io.scalaland.chimney.dsl.transformInto
import net.wiringbits.api.models.admin.{AdminGetUserLogs, AdminGetUsers}
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
      items = logs.map(_.transformInto[AdminGetUserLogs.Response.UserLog])
    } yield AdminGetUserLogs.Response(items)
  }

  def users(): Future[AdminGetUsers.Response] = {
    for {
      users <- usersRepository.all()
      items = users.map(_.transformInto[AdminGetUsers.Response.User])
    } yield AdminGetUsers.Response(items)
  }
}

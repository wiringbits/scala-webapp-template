package net.wiringbits.services

import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.repositories.UserLogsRepository

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserLogsService @Inject() (userLogsRepository: UserLogsRepository)(implicit
    ec: ExecutionContext
) {

  def logs(userId: UUID): Future[GetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(userId)
      items = logs.map { x =>
        GetUserLogs.Response.UserLog(
          id = x.userLogId,
          message = x.message,
          createdAt = x.createdAt
        )
      }
    } yield GetUserLogs.Response(items)
  }
}

package net.wiringbits.services

import net.wiringbits.api.models.GetUserLogsResponse
import net.wiringbits.repositories.UserLogsRepository

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserLogsService @Inject() (userLogsRepository: UserLogsRepository)(implicit
    ec: ExecutionContext
) {

  def logs(userId: UUID): Future[GetUserLogsResponse] = {
    for {
      logs <- userLogsRepository.logs(userId)
      items = logs.map { x =>
        GetUserLogsResponse.UserLog(
          id = x.userLogId,
          message = x.message,
          createdAt = x.createdAt
        )
      }
    } yield GetUserLogsResponse(items)
  }
}

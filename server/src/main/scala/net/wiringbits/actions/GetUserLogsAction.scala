package net.wiringbits.actions

import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.common.models.UUIDCustom
import net.wiringbits.repositories.UserLogsRepository

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserLogsAction @Inject() (
    userLogsRepository: UserLogsRepository
)(implicit ec: ExecutionContext) {

  def apply(userId: UUIDCustom): Future[GetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(userId)
      items = logs.map { x =>
        GetUserLogs.Response.UserLog(
          id = x.userLogId.value,
          message = x.message,
          createdAt = x.createdAt.value
        )
      }
    } yield GetUserLogs.Response(items)
  }
}

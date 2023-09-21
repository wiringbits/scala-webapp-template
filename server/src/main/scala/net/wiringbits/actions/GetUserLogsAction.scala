package net.wiringbits.actions

import io.scalaland.chimney.dsl.transformInto
import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.common.models.id.UserId
import net.wiringbits.repositories.UserLogsRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserLogsAction @Inject() (
    userLogsRepository: UserLogsRepository
)(implicit ec: ExecutionContext) {

  def apply(userId: UserId): Future[GetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(userId)
      // TODO: use chimney after creating our own types
      items = logs.map { x =>
        GetUserLogs.Response.UserLog(
          userLogId = x.userLogId,
          message = x.message,
          createdAt = x.createdAt.value
        )
      }
    } yield GetUserLogs.Response(items)
  }
}

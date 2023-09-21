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
      items = logs.transformInto[List[GetUserLogs.Response.UserLog]]
    } yield GetUserLogs.Response(items)
  }
}

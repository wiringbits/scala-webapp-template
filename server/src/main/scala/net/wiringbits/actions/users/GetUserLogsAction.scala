package net.wiringbits.actions.users

import io.scalaland.chimney.dsl.transformInto
import net.wiringbits.api.models.users.GetUserLogs
import net.wiringbits.repositories.UserLogsRepository

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserLogsAction @Inject() (
    userLogsRepository: UserLogsRepository
)(implicit ec: ExecutionContext) {

  def apply(userId: UUID): Future[GetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(userId)
      items = logs.map(_.transformInto[GetUserLogs.Response.UserLog])
    } yield GetUserLogs.Response(items)
  }
}

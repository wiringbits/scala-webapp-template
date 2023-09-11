package net.wiringbits.actions

import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.repositories.UserLogsRepository
import net.wiringbits.typo_generated.public.users.UsersId

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserLogsAction @Inject() (
    userLogsRepository: UserLogsRepository
)(implicit ec: ExecutionContext) {

  def apply(usersId: UsersId): Future[GetUserLogs.Response] = {
    for {
      logs <- userLogsRepository.logs(usersId)
      items = logs.map { x =>
        GetUserLogs.Response.UserLog(
          id = x.userLogId.value.value,
          message = x.message,
          createdAt = x.createdAt.value.toInstant
        )
      }
    } yield GetUserLogs.Response(items)
  }
}

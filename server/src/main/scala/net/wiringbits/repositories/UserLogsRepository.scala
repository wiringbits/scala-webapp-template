package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.typo_generated.public.user_logs.{UserLogsRepoImpl, UserLogsRow}
import play.api.db.Database

import java.time.{Clock, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class UserLogsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {

  def create(userLogsRow: UserLogsRow): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserLogsRepoImpl.insert(userLogsRow)
    }
  }

  def create(userId: UUID, message: String): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UUID.randomUUID(),
      userId = userId,
      message = message,
      createdAt = clock.instant()
    )

    database.withConnection { implicit conn =>
      UserLogsRepoImpl.insert(createUserLogsRow)
    }
  }

  def logs(userId: UUID): Future[List[UserLogsRow]] = Future {
    database.withConnection { implicit conn =>
      UserLogsRepoImpl.select.where(_.userId === userId).orderBy(_.createdAt.desc).toList
    }
  }
}

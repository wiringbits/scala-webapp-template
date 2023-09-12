package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.typo_generated.customtypes.{TypoOffsetDateTime, TypoUUID}
import net.wiringbits.typo_generated.public.user_logs.{UserLogsId, UserLogsRepoImpl, UserLogsRow}
import net.wiringbits.typo_generated.public.users.UsersId
import play.api.db.Database

import java.time.{Clock, ZoneOffset}
import javax.inject.Inject
import scala.concurrent.Future

class UserLogsRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext, clock: Clock) {

  def create(userLogsRow: UserLogsRow): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserLogsRepoImpl.insert(userLogsRow)
    }
  }

  def create(usersId: UsersId, message: String): Future[Unit] = Future {
    val createUserLogsRow = UserLogsRow(
      userLogId = UserLogsId(TypoUUID.randomUUID),
      userId = usersId,
      message = message,
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    database.withConnection { implicit conn =>
      UserLogsRepoImpl.insert(createUserLogsRow)
    }
  }

  def logs(usersId: UsersId): Future[List[UserLogsRow]] = Future {
    database.withConnection { implicit conn =>
      UserLogsRepoImpl.select.where(_.userId === usersId).orderBy(_.createdAt.desc).toList
    }
  }
}

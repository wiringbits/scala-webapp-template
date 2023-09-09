package net.wiringbits.actions

import net.wiringbits.api.models.UpdatePassword
import net.wiringbits.common.models.Name
import net.wiringbits.util.{EmailMessage, EmailsHelper}
import net.wiringbits.validations.ValidatePasswordMatches
import org.foo.generated.customtypes.{TypoOffsetDateTime, TypoUUID}
import org.foo.generated.public.user_logs.{UserLogsId, UserLogsRepoImpl, UserLogsRow}
import org.foo.generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.Database

import java.sql.Connection
import java.time.{Clock, OffsetDateTime, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdatePasswordAction @Inject() (
    database: Database
)(implicit ec: ExecutionContext, clock: Clock) {
  given c: Connection = database.getConnection()

  def apply(userId: UUID, request: UpdatePassword.Request): Future[Unit] = {
    for {
      maybe <- Future(UsersRepoImpl.selectById(UsersId(TypoUUID(userId))))
      user = ValidatePasswordMatches(maybe, request.oldPassword)
      hashedPassword = BCrypt.hashpw(request.newPassword.string, BCrypt.gensalt())
      emailMessage = EmailMessage.updatePassword(Name.trusted(user.name))
      _ <- updatePassword(usersRow = user, hashedPassword = hashedPassword, emailMessage = emailMessage)
    } yield ()
  }

  private def updatePassword(usersRow: UsersRow, hashedPassword: String, emailMessage: EmailMessage): Future[Unit] = {
    for {
      _ <- Future(UsersRepoImpl.update(usersRow.copy(password = hashedPassword)))
      createUserLogs = UserLogsRow(
        userLogId = UserLogsId(TypoUUID(UUID.randomUUID())),
        userId = usersRow.userId,
        message = "Password was updated",
        createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
      )
      _ <- Future(UserLogsRepoImpl.insert(createUserLogs))
      _ <- EmailsHelper.sendEmailLater(usersRow.userId, emailMessage)
    } yield ()
  }
}

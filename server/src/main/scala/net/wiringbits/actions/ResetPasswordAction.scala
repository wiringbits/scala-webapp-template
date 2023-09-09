package net.wiringbits.actions

import net.wiringbits.api.models.ResetPassword
import net.wiringbits.common.models.{Email, Name, Password}
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.{UserTokensRepository, UsersRepository}
import net.wiringbits.util.{EmailMessage, EmailsHelper, TokensHelper}
import net.wiringbits.validations.ValidateUserToken
import net.wiringbits.typo_generated.customtypes.{TypoOffsetDateTime, TypoUUID}
import net.wiringbits.typo_generated.public.user_logs.{UserLogsId, UserLogsRepoImpl, UserLogsRow}
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensId, UserTokensRepoImpl}
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.Database

import java.sql.Connection
import java.time.{Clock, OffsetDateTime, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ResetPasswordAction @Inject() (
    userTokensConfig: UserTokensConfig,
    database: Database
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {
  given c: Connection = database.getConnection()

  def apply(userId: UUID, token: UUID, password: Password): Future[ResetPassword.Response] = {
    val hashedPassword = BCrypt.hashpw(password.string, BCrypt.gensalt())
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    for {
      // When the token valid
      token <- Future(
        UserTokensRepoImpl.select
          .where(_.userId === UsersId(TypoUUID(userId)))
          .where(_.token === hmacToken)
          .limit(1)
          .toList
          .headOption
          .getOrElse(throw new RuntimeException(s"Token for user $userId wasn't found"))
      )
      _ = ValidateUserToken(token)

      // We trigger the reset password flow
      user <- Future(
        UsersRepoImpl
          .selectById(UsersId(TypoUUID(userId)))
          .getOrElse(throw new RuntimeException(s"User with id $userId wasn't found"))
      )
      emailMessage = EmailMessage.resetPassword(Name.trusted(user.name))
      _ <- resetPassword(user, hashedPassword, emailMessage)
    } yield ResetPassword.Response(name = Name.trusted(user.name), email = Email.trusted(user.email.value))
  }

  private def resetPassword(usersRow: UsersRow, hashedPassword: String, emailMessage: EmailMessage): Future[Unit] = {
    for {
      _ <- Future(UsersRepoImpl.update(usersRow.copy(password = hashedPassword)))
      createUserLogs = UserLogsRow(
        userLogId = UserLogsId(TypoUUID(UUID.randomUUID())),
        userId = usersRow.userId,
        message = "Password was reset",
        createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
      )
      _ <- Future(UserLogsRepoImpl.insert(createUserLogs))
      _ <- EmailsHelper.sendEmailLater(usersRow.userId, emailMessage)
    } yield ()
  }
}

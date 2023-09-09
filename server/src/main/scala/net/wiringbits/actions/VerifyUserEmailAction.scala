package net.wiringbits.actions

import net.wiringbits.api.models.VerifyEmail
import net.wiringbits.common.models.Name
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.{UserTokensRepository, UsersRepository}
import net.wiringbits.util.{EmailMessage, EmailsHelper, TokensHelper}
import net.wiringbits.validations.{ValidateUserIsNotVerified, ValidateUserToken}
import net.wiringbits.typo_generated.customtypes.{TypoOffsetDateTime, TypoUUID}
import net.wiringbits.typo_generated.public.user_logs.{UserLogsId, UserLogsRepoImpl, UserLogsRow}
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensId, UserTokensRepoImpl}
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import play.api.db.Database

import java.sql.Connection
import java.time.{Clock, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VerifyUserEmailAction @Inject() (
    userTokensConfig: UserTokensConfig,
    database: Database
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {
  given c: Connection = database.getConnection()

  def apply(userId: UUID, token: UUID): Future[VerifyEmail.Response] = for {
    // when the user is not verified
    user <- Future(
      UsersRepoImpl.selectById(UsersId(TypoUUID(userId))).getOrElse(throw new RuntimeException("User wasn't found"))
    )
    _ = ValidateUserIsNotVerified(user)

    // the token is validated
    hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    userToken <- Future(
      UserTokensRepoImpl.select
        .where(_.userId === user.userId)
        .where(_.token === hmacToken)
        .limit(1)
        .toList
        .headOption
        .getOrElse(throw new RuntimeException(s"Token for user $userId wasn't found"))
    )
    _ = ValidateUserToken(userToken)

    // then, the user is marked as verified
    emailMessage = EmailMessage.confirm(Name.trusted(user.name))
    _ <- verifyUser(usersRow = user, userTokensId = userToken.userTokenId, emailMessage = emailMessage)
  } yield VerifyEmail.Response()

  private def verifyUser(usersRow: UsersRow, userTokensId: UserTokensId, emailMessage: EmailMessage): Future[Unit] = {
    for {
      _ <- Future(
        UsersRepoImpl.update(
          usersRow.copy(verifiedOn = Some(TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))))
        )
      )
      createUserLogs = UserLogsRow(
        userLogId = UserLogsId(TypoUUID.randomUUID),
        userId = usersRow.userId,
        message = "Email verified",
        createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
      )
      _ <- Future(UserLogsRepoImpl.insert(createUserLogs))
      _ <- Future(UserTokensRepoImpl.delete(userTokensId))
      _ <- EmailsHelper.sendEmailLater(usersRow.userId, emailMessage)
    } yield ()
  }
}

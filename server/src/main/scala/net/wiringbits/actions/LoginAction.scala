package net.wiringbits.actions

import net.wiringbits.api.models.Login
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.repositories.{UserLogsRepository, UsersRepository}
import net.wiringbits.validations.{ValidateCaptcha, ValidatePasswordMatches, ValidateVerifiedUser}
import net.wiringbits.typo_generated.customtypes.{TypoOffsetDateTime, TypoUUID, TypoUnknownCitext}
import net.wiringbits.typo_generated.public.user_logs.{UserLogsId, UserLogsRepoImpl, UserLogsRow}
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRepoImpl}
import play.api.db.Database

import java.sql.Connection
import java.time.{Clock, OffsetDateTime, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LoginAction @Inject() (
    captchaApi: ReCaptchaApi,
    database: Database
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {
  given c: Connection = database.getConnection()

  // returns the token to use for authenticating requests
  def apply(request: Login.Request): Future[Login.Response] = {
    for {
      _ <- ValidateCaptcha(captchaApi, request.captcha)
      // the user is verified
      maybe <- Future(UsersRepoImpl.select.where(_.email === TypoUnknownCitext(request.email.string)).toList.headOption)
      _ = maybe.foreach(ValidateVerifiedUser(_))

      // The password matches
      user = ValidatePasswordMatches(maybe, request.password)

      // A login token is created
      createUserLogs = UserLogsRow(
        userLogId = UserLogsId(TypoUUID.randomUUID),
        userId = user.userId,
        message = "Logged in successfully",
        createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
      )
      _ <- Future(UserLogsRepoImpl.insert(createUserLogs))
    } yield Login.Response(user.userId.value.value, Name.trusted(user.name), Email.trusted(user.email.value))
  }
}

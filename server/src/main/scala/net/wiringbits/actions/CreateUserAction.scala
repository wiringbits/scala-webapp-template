package net.wiringbits.actions

import net.wiringbits.api.models.CreateUser
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories
import net.wiringbits.repositories.models.UserTokenType
import net.wiringbits.util.{EmailsHelper, TokenGenerator, TokensHelper}
import net.wiringbits.validations.{ValidateCaptcha, ValidateEmailIsAvailable}
import net.wiringbits.typo_generated.customtypes.{TypoOffsetDateTime, TypoUUID, TypoUnknownCitext}
import net.wiringbits.typo_generated.public.user_logs.{UserLogsId, UserLogsRepoImpl, UserLogsRow}
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensId, UserTokensRepoImpl, UserTokensRow}
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.Database

import java.sql.Connection
import java.time.temporal.ChronoUnit
import java.time.{Clock, OffsetDateTime, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateUserAction @Inject() (
    reCaptchaApi: ReCaptchaApi,
    tokenGenerator: TokenGenerator,
    userTokensConfig: UserTokensConfig,
    emailsHelper: EmailsHelper,
    database: Database
)(implicit
    clock: Clock,
    ec: ExecutionContext
) {
  given db: Connection = database.getConnection()

  def apply(request: CreateUser.Request): Future[CreateUser.Response] = {
    for {
      _ <- validations(request)
      hashedPassword = BCrypt.hashpw(request.password.string, BCrypt.gensalt())
      token = tokenGenerator.next()
      hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes(), userTokensConfig.hmacSecret)

      // create the user
      createUser <- createUser(request = request, hashedPassword = hashedPassword, hmacToken = hmacToken)

      // then, send the verification email
      _ <- emailsHelper.sendRegistrationEmailWithVerificationToken(createUser, token)
    } yield CreateUser.Response(
      id = createUser.userId.value.value,
      email = request.email,
      name = request.name
    )
  }

  private def validations(request: CreateUser.Request) = {
    for {
      _ <- ValidateCaptcha(reCaptchaApi, request.captcha)
      _ <- ValidateEmailIsAvailable(request.email)
    } yield ()
  }

  private def createUser(request: CreateUser.Request, hashedPassword: String, hmacToken: String): Future[UsersRow] = {
    val createUser = UsersRow(
      userId = UsersId(TypoUUID(UUID.randomUUID())),
      name = request.name.string,
      lastName = None,
      email = TypoUnknownCitext(request.email.string),
      password = hashedPassword,
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
      verifiedOn = None
    )

    val createToken = UserTokensRow(
      userTokenId = UserTokensId(TypoUUID(UUID.randomUUID())),
      token = hmacToken,
      tokenType = UserTokenType.EmailVerification.toString,
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC)),
      expiresAt = TypoOffsetDateTime(
        clock.instant().atOffset(ZoneOffset.UTC).plus(userTokensConfig.emailVerificationExp.toHours, ChronoUnit.HOURS)
      ),
      userId = createUser.userId
    )

    val createUserLogs = UserLogsRow(
      userLogId = UserLogsId(TypoUUID(UUID.randomUUID())),
      userId = createUser.userId,
      message = s"Account created, name = ${request.name}, email = ${request.email}",
      createdAt = TypoOffsetDateTime(clock.instant().atOffset(ZoneOffset.UTC))
    )

    for {
      user <- Future(UsersRepoImpl.insert(createUser))
      _ <- Future(UserTokensRepoImpl.insert(createToken))
      _ <- Future(UserLogsRepoImpl.insert(createUserLogs))
    } yield user
  }
}

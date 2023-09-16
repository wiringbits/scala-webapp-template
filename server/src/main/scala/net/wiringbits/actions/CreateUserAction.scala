package net.wiringbits.actions

import net.wiringbits.api.models.CreateUser
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.common.models.{InstantCustom, UUIDCustom}
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.repositories.models.User
import net.wiringbits.typo_generated.public.users.UsersRow
import net.wiringbits.util.{EmailsHelper, TokenGenerator, TokensHelper}
import net.wiringbits.validations.{ValidateCaptcha, ValidateEmailIsAvailable}
import org.mindrot.jbcrypt.BCrypt

import java.time.{Clock, Instant, ZoneOffset}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateUserAction @Inject() (
    usersRepository: UsersRepository,
    reCaptchaApi: ReCaptchaApi,
    tokenGenerator: TokenGenerator,
    userTokensConfig: UserTokensConfig,
    emailsHelper: EmailsHelper
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {

  def apply(request: CreateUser.Request): Future[CreateUser.Response] = {
    for {
      _ <- validations(request)
      hashedPassword = BCrypt.hashpw(request.password.string, BCrypt.gensalt())
      token = tokenGenerator.next()
        hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes(), userTokensConfig.hmacSecret)

      // create the user
      createUsersRow = UsersRow(
        userId = UUIDCustom.randomUUID(),
        name = request.name,
        lastName = None,
        email = request.email,
        password = hashedPassword,
        createdAt = InstantCustom.fromClock,
        verifiedOn = None
      )
      _ <- usersRepository.create(createUsersRow, hmacToken)

      // then, send the verification email
      _ <- emailsHelper.sendRegistrationEmailWithVerificationToken(
        createUsersRow,
        token
      )
    } yield CreateUser.Response(
      id = createUsersRow.userId.value,
      email = request.email,
      name = request.name
    )
  }

  private def validations(request: CreateUser.Request) = {
    for {
      _ <- ValidateCaptcha(reCaptchaApi, request.captcha)
      _ <- ValidateEmailIsAvailable(usersRepository, request.email)
    } yield ()
  }
}

package net.wiringbits.actions

import net.wiringbits.api.models.CreateUser
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.apis.{EmailApi, ReCaptchaApi}
import net.wiringbits.config.{UserTokensConfig, WebAppConfig}
import net.wiringbits.repositories
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.util.{EmailMessage, TokenGenerator, TokensHelper}
import net.wiringbits.validations.{ValidateCaptcha, ValidateEmailIsAvailable}
import org.mindrot.jbcrypt.BCrypt

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateUserAction @Inject() (
    usersRepository: UsersRepository,
    reCaptchaApi: ReCaptchaApi,
    tokenGenerator: TokenGenerator,
    userTokensConfig: UserTokensConfig,
    webAppConfig: WebAppConfig,
    emailApi: EmailApi
)(implicit
    ec: ExecutionContext
) {

  def apply(request: CreateUser.Request): Future[CreateUser.Response] = {
    for {
      _ <- validations(request)
      hashedPassword = BCrypt.hashpw(request.password.string, BCrypt.gensalt())
      token = tokenGenerator.next()
      hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes(), userTokensConfig.hmacSecret)

      // create the user
      createUser = repositories.models.User
        .CreateUser(
          id = UUID.randomUUID(),
          name = request.name,
          email = request.email,
          hashedPassword = hashedPassword,
          verifyEmailToken = hmacToken
        )
      _ <- usersRepository.create(createUser)

      // then, send the verification email
      emailParameter = s"${createUser.id}_$token"
      emailMessage = EmailMessage.registration(
        name = createUser.name,
        url = webAppConfig.host,
        emailParameter = emailParameter
      )
      _ <- emailApi.sendEmail(EmailRequest(request.email, emailMessage))
    } yield CreateUser.Response(id = createUser.id, email = createUser.email, name = createUser.name)
  }

  private def validations(request: CreateUser.Request) = {
    for {
      _ <- ValidateCaptcha(reCaptchaApi, request.captcha)
      _ <- ValidateEmailIsAvailable(usersRepository, request.email)
    } yield ()
  }
}

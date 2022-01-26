package net.wiringbits.actions

import net.wiringbits.api.models.ResetPassword
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.common.models.Password
import net.wiringbits.config.{JwtConfig, UserTokensConfig}
import net.wiringbits.repositories.{UserTokensRepository, UsersRepository}
import net.wiringbits.util.{EmailMessage, JwtUtils, TokensHelper}
import net.wiringbits.validations.ValidateUserToken
import org.mindrot.jbcrypt.BCrypt

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ResetPasswordAction @Inject() (
    userTokensConfig: UserTokensConfig,
    jwtConfig: JwtConfig,
    usersRepository: UsersRepository,
    emailApi: EmailApi,
    userTokensRepository: UserTokensRepository
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {

  def apply(userId: UUID, token: UUID, password: Password): Future[ResetPassword.Response] = {
    val hashedPassword = BCrypt.hashpw(password.string, BCrypt.gensalt())
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    for {
      // When the token valid
      tokenMaybe <- userTokensRepository.find(userId, hmacToken)
      token = tokenMaybe.getOrElse(throw new RuntimeException(s"Token for user $userId wasn't found"))
      _ = ValidateUserToken(token)

      // We trigger the reset password flow
      userMaybe <- usersRepository.find(userId)
      user = userMaybe.getOrElse(throw new RuntimeException(s"User with id $userId wasn't found"))
      _ <- usersRepository.resetPassword(userId, hashedPassword)

      // Sending a confirmation email
      emailMessage = EmailMessage.resetPassword(user.name)
      _ = emailApi.sendEmail(EmailRequest(user.email, emailMessage))

      // And, returning the auth token
      jwt = JwtUtils.createToken(jwtConfig, user.id)(clock)
    } yield ResetPassword.Response(name = user.name, email = user.email, token = jwt)
  }
}

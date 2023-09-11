package net.wiringbits.actions

import net.wiringbits.api.models.ResetPassword
import net.wiringbits.common.models.{Email, Name, Password}
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.{UserTokensRepository, UsersRepository}
import net.wiringbits.typo_generated.public.users.UsersId
import net.wiringbits.util.{EmailMessage, TokensHelper}
import net.wiringbits.validations.ValidateUserToken
import org.mindrot.jbcrypt.BCrypt

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ResetPasswordAction @Inject() (
    userTokensConfig: UserTokensConfig,
    usersRepository: UsersRepository,
    userTokensRepository: UserTokensRepository
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {

  def apply(usersId: UsersId, token: UUID, password: Password): Future[ResetPassword.Response] = {
    val hashedPassword = BCrypt.hashpw(password.string, BCrypt.gensalt())
    val hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    for {
      // When the token valid
      tokenMaybe <- userTokensRepository.find(usersId, hmacToken)
      token = tokenMaybe.getOrElse(throw new RuntimeException(s"Token for user $usersId wasn't found"))
      _ = ValidateUserToken(token)

      // We trigger the reset password flow
      userMaybe <- usersRepository.find(usersId)
      user = userMaybe.getOrElse(throw new RuntimeException(s"User with id $usersId wasn't found"))
      emailMessage = EmailMessage.resetPassword(Name.trusted(user.name))
      _ <- usersRepository.resetPassword(usersId, hashedPassword, emailMessage)
    } yield ResetPassword.Response(name = Name.trusted(user.name), email = Email.trusted(user.email.value))
  }
}

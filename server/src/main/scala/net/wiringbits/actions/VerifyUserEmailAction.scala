package net.wiringbits.actions

import net.wiringbits.api.models.VerifyEmail
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.{UserTokensRepository, UsersRepository}
import net.wiringbits.util.{EmailMessage, TokensHelper}
import net.wiringbits.validations.{ValidateUserIsNotVerified, ValidateUserToken}

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VerifyUserEmailAction @Inject() (
    usersRepository: UsersRepository,
    userTokensRepository: UserTokensRepository,
    userTokensConfig: UserTokensConfig,
    emailApi: EmailApi
)(implicit
    ec: ExecutionContext,
    clock: Clock
) {
  def apply(userId: UUID, token: UUID): Future[VerifyEmail.Response] = for {
    // when the user is not verified
    userMaybe <- usersRepository.find(userId)
    user = userMaybe.getOrElse(throw new RuntimeException(s"User wasn't found"))
    _ = ValidateUserIsNotVerified(user)

    // the token is validated
    hmacToken = TokensHelper.doHMACSHA1(token.toString.getBytes, userTokensConfig.hmacSecret)
    tokenMaybe <- userTokensRepository.find(userId, hmacToken)
    userToken = tokenMaybe.getOrElse(throw new RuntimeException(s"Token for user $userId wasn't found"))
    _ = ValidateUserToken(userToken)

    // then, the user is marked as verified
    _ <- usersRepository.verify(userId = userId, tokenId = userToken.id)
    _ = emailApi.sendEmail(EmailRequest(user.email, EmailMessage.confirm(user.name)))
  } yield VerifyEmail.Response()
}

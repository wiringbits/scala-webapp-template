package net.wiringbits.actions

import net.wiringbits.api.models.VerifyEmail
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories.{UserTokensRepository, UsersRepository}
import net.wiringbits.util.{EmailMessage, TokensHelper}
import net.wiringbits.validations.ValidateUserIsNotVerified

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VerifyUserEmailAction @Inject() (
    usersRepository: UsersRepository,
    userTokensRepository: UserTokensRepository,
    userTokensConfig: UserTokensConfig
)(implicit
    ec: ExecutionContext
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

    // then, the user is marked as verified
    emailMessage = EmailMessage.confirm(user.name)
    _ <- usersRepository.verify(userId = userId, tokenId = userToken.id, emailMessage)
  } yield VerifyEmail.Response()
}

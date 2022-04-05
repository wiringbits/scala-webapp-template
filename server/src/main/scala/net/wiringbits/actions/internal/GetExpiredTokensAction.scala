package net.wiringbits.actions.internal

import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.repositories.models.UserToken

import javax.inject.Inject
import scala.concurrent.Future

class GetExpiredTokensAction @Inject() (
    userTokensRepository: UserTokensRepository
) {
  def apply(): Future[List[UserToken]] = {
    userTokensRepository.getExpiredTokens
  }
}

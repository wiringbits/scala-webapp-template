package net.wiringbits.actions.internal

import net.wiringbits.repositories.UserTokensRepository
import net.wiringbits.repositories.models.UserToken

import javax.inject.Inject
import scala.concurrent.Future

class DeleteExpiredTokenAction @Inject() (userTokensRepository: UserTokensRepository) {
  def apply(token: UserToken): Future[Unit] = {
    userTokensRepository.delete(tokenId = token.id, userId = token.userId)
  }
}

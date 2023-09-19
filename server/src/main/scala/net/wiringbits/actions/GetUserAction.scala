package net.wiringbits.actions

import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.common.models.id.UserId
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.typo_generated.public.users.UsersRow

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserAction @Inject() (
    usersRepository: UsersRepository
)(implicit ec: ExecutionContext) {

  def apply(userId: UserId): Future[GetCurrentUser.Response] = {
    for {
      user <- unsafeUser(userId)
    } yield GetCurrentUser.Response(
      userId = user.userId,
      email = user.email,
      name = user.name,
      createdAt = user.createdAt
    )
  }

  private def unsafeUser(userId: UserId): Future[UsersRow] = {
    usersRepository
      .find(userId)
      .map { maybe =>
        maybe.getOrElse(
          throw new RuntimeException(
            s"Unexpected error because the user wasn't found: $userId"
          )
        )
      }
  }
}

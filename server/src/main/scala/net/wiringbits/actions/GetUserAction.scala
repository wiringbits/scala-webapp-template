package net.wiringbits.actions

import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.repositories.models.User
import net.wiringbits.typo_generated.public.users.UsersRow

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserAction @Inject() (
    usersRepository: UsersRepository
)(implicit ec: ExecutionContext) {

  def apply(userId: UUID): Future[GetCurrentUser.Response] = {
    for {
      user <- unsafeUser(userId)
    } yield GetCurrentUser.Response(
      id = user.userId,
      email = user.email,
      name = user.name,
      createdAt = user.createdAt
    )
  }

  private def unsafeUser(userId: UUID): Future[UsersRow] = {
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

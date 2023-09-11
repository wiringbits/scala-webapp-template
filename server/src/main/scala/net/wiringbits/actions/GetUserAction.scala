package net.wiringbits.actions

import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.common.models.{Email, Name}
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.repositories.models.User
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRow}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserAction @Inject() (
    usersRepository: UsersRepository
)(implicit ec: ExecutionContext) {

  def apply(usersId: UsersId): Future[GetCurrentUser.Response] = {
    for {
      user <- unsafeUser(usersId)
    } yield GetCurrentUser.Response(
      id = user.userId.value.value,
      email = Email.trusted(user.email.value),
      name = Name.trusted(user.name),
      createdAt = user.createdAt.value.toInstant
    )
  }

  private def unsafeUser(usersId: UsersId): Future[UsersRow] = {
    usersRepository
      .find(usersId)
      .map { maybe =>
        maybe.getOrElse(
          throw new RuntimeException(
            s"Unexpected error because the user wasn't found: $usersId"
          )
        )
      }
  }
}

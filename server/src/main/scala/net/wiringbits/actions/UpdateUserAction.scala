package net.wiringbits.actions

import net.wiringbits.api.models.UpdateUser
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.typo_generated.public.users.UsersId

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdateUserAction @Inject() (
    usersRepository: UsersRepository
)(implicit ec: ExecutionContext) {

  def apply(usersId: UsersId, request: UpdateUser.Request): Future[Unit] = {
    val validate = Future {
      if (request.name.string.isEmpty) new RuntimeException(s"The name is required")
      else ()
    }

    for {
      _ <- validate
      _ <- usersRepository.update(usersId, request.name)
    } yield ()
  }
}

package net.wiringbits.actions

import net.wiringbits.api.models.UpdatePassword
import net.wiringbits.common.models.Name
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.typo_generated.public.users.UsersId
import net.wiringbits.util.EmailMessage
import net.wiringbits.validations.ValidatePasswordMatches
import org.mindrot.jbcrypt.BCrypt

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdatePasswordAction @Inject() (
    usersRepository: UsersRepository
)(implicit ec: ExecutionContext) {

  def apply(usersId: UsersId, request: UpdatePassword.Request): Future[Unit] = {
    for {
      maybe <- usersRepository.find(usersId)
      user = ValidatePasswordMatches(maybe, request.oldPassword)
      hashedPassword = BCrypt.hashpw(request.newPassword.string, BCrypt.gensalt())
      emailMessage = EmailMessage.updatePassword(Name.trusted(user.name))
      _ <- usersRepository.updatePassword(usersId, hashedPassword, emailMessage)
    } yield ()
  }
}

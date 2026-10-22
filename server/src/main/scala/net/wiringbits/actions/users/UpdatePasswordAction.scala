package net.wiringbits.actions.users

import net.wiringbits.api.models.users.UpdatePassword
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.services.PasswordHasher
import net.wiringbits.util.EmailMessage
import net.wiringbits.validations.ValidatePasswordMatches

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdatePasswordAction @Inject() (
    usersRepository: UsersRepository,
    passwordHasher: PasswordHasher
)(implicit ec: ExecutionContext) {

  def apply(userId: UUID, request: UpdatePassword.Request): Future[Unit] = {
    for {
      maybe <- usersRepository.find(userId)
      user = ValidatePasswordMatches(maybe, request.oldPassword, passwordHasher)
      hashedPassword = passwordHasher.hash(request.newPassword.string)
      emailMessage = EmailMessage.updatePassword(user.name)
      _ <- usersRepository.updatePassword(userId, hashedPassword, emailMessage)
    } yield ()
  }
}

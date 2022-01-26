package net.wiringbits.actions

import net.wiringbits.api.models.UpdatePassword
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.util.EmailMessage
import net.wiringbits.validations.ValidatePassword
import org.mindrot.jbcrypt.BCrypt

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdatePasswordAction @Inject() (
    usersRepository: UsersRepository,
    emailApi: EmailApi
)(implicit ec: ExecutionContext) {

  def apply(userId: UUID, request: UpdatePassword.Request): Future[Unit] = {
    for {
      maybe <- usersRepository.find(userId)
      user = ValidatePassword(maybe, request.oldPassword)
      hashedPassword = BCrypt.hashpw(request.newPassword.string, BCrypt.gensalt())
      _ <- usersRepository.updatePassword(userId, hashedPassword)
      emailMessage = EmailMessage.updatePassword(user.name)
      _ = emailApi.sendEmail(EmailRequest(user.email, emailMessage))
    } yield ()
  }
}

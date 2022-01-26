package net.wiringbits.actions

import net.wiringbits.api.models.UpdatePassword
import net.wiringbits.apis.EmailApi
import net.wiringbits.apis.models.EmailRequest
import net.wiringbits.repositories.UsersRepository
import net.wiringbits.util.EmailMessage
import org.mindrot.jbcrypt.BCrypt

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdatePasswordAction @Inject() (
    usersRepository: UsersRepository,
    emailApi: EmailApi
)(implicit ec: ExecutionContext) {

  def apply(userId: UUID, request: UpdatePassword.Request): Future[Unit] = {
    val validate = Future {
      if (request.newPassword.string.isEmpty) new RuntimeException(s"The password is required")
      else ()
    }

    for {
      _ <- validate
      userMaybe <- usersRepository.find(userId)
      user = userMaybe
        .filter(user => BCrypt.checkpw(request.oldPassword.string, user.hashedPassword))
        .getOrElse(throw new RuntimeException("The given email/password doesn't match"))
      hashedPassword = BCrypt.hashpw(request.newPassword.string, BCrypt.gensalt())
      _ <- usersRepository.updatePassword(userId, hashedPassword)
      emailMessage = EmailMessage.updatePassword(user.name)
      _ = emailApi.sendEmail(EmailRequest(user.email, emailMessage))
    } yield ()
  }
}

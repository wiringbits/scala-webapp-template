package net.wiringbits.validations

import net.wiringbits.common.models.Email
import net.wiringbits.repositories.UsersRepository

import scala.concurrent.{ExecutionContext, Future}

object ValidateEmailIsAvailable {
  def apply(repository: UsersRepository, email: Email)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      maybe <- repository.find(email)
    } yield {
      if (maybe.isDefined) throw new RuntimeException(s"The email is not available")
      else ()
    }
  }
}

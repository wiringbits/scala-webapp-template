package net.wiringbits.validations

import net.wiringbits.common.models.Email
import net.wiringbits.repositories.UsersRepository

import scala.concurrent.{ExecutionContext, Future}

object ValidateEmailIsRegistered {
  def apply(repository: UsersRepository, email: Email)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      maybe <- repository.find(email)
    } yield {
      if (maybe.isEmpty) throw new RuntimeException(s"The email is not registered")
      else ()
    }
  }
}

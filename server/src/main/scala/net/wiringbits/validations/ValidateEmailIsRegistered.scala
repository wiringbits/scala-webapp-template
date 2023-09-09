package net.wiringbits.validations

import net.wiringbits.common.models.Email
import net.wiringbits.repositories.UsersRepository
import org.foo.generated.customtypes.TypoUnknownCitext
import org.foo.generated.public.users.UsersRepoImpl

import java.sql.Connection
import scala.concurrent.{ExecutionContext, Future}

object ValidateEmailIsRegistered {
  def apply(email: Email)(using ec: ExecutionContext, conn: Connection): Future[Unit] = {
    for {
      maybe <- Future(UsersRepoImpl.select.where(_.email === TypoUnknownCitext(email.string)).toList.headOption)
    } yield {
      if (maybe.isEmpty) throw new RuntimeException(s"The email is not registered")
      else ()
    }
  }
}

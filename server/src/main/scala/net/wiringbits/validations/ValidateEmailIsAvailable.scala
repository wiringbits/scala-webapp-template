package net.wiringbits.validations

import net.wiringbits.common.models.Email
import org.foo.generated.customtypes.TypoUnknownCitext
import org.foo.generated.public.users.UsersRepoImpl

import java.sql.Connection
import scala.concurrent.{ExecutionContext, Future}

object ValidateEmailIsAvailable {
  def apply(email: Email)(using ec: ExecutionContext, con: Connection): Future[Unit] = {
    for {
      maybe <- Future(UsersRepoImpl.select.where(_.email === TypoUnknownCitext(email.string)).toList.headOption)
    } yield {
      if (maybe.isDefined) throw new RuntimeException(s"The email is not available")
      else ()
    }
  }
}

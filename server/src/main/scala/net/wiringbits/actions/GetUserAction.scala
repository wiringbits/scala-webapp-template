package net.wiringbits.actions

import net.wiringbits.api.models.GetCurrentUser
import net.wiringbits.common.models.{Email, Name}
import org.foo.generated.customtypes.TypoUUID
import org.foo.generated.public.users.{UsersId, UsersRepoImpl, UsersRow}
import play.api.db.Database

import java.sql.Connection
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserAction @Inject() (database: Database)(implicit ec: ExecutionContext) {
  given c: Connection = database.getConnection()

  def apply(userId: UUID): Future[GetCurrentUser.Response] = {
    for {
      user <- unsafeUser(userId)
    } yield GetCurrentUser.Response(
      id = user.userId.value.value,
      email = Email.trusted(user.email.value),
      name = Name.trusted(user.name),
      createdAt = user.createdAt.value.toInstant
    )
  }

  private def unsafeUser(userId: UUID): Future[UsersRow] = Future {
    UsersRepoImpl
      .selectById(UsersId(TypoUUID(userId)))
      .getOrElse(
        throw new RuntimeException(
          s"Unexpected error because the user wasn't found: $userId"
        )
      )
  }
}

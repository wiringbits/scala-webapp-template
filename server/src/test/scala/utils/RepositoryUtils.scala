package utils

import net.wiringbits.common.models.{Email, InstantCustom, Name, UUIDCustom}
import net.wiringbits.core.RepositoryComponents
import net.wiringbits.typo_generated.public.users.UsersRow

import java.time.Instant
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait RepositoryUtils {
  def createNonVerifyUser(userIdMaybe: Option[UUIDCustom] = None, emailMaybe: Option[Email] = None)(using
      ec: ExecutionContext,
      repositories: RepositoryComponents
  ): Future[UsersRow] = {
    val userId = userIdMaybe.getOrElse(UUIDCustom.randomUUID())
    val email = emailMaybe.getOrElse(Email.trusted("hello@wiringbits.net"))

    val createUserRow = UsersRow(
      userId = userId,
      name = Name.trusted("Sample"),
      lastName = None,
      email = email,
      password = "password",
      createdAt = InstantCustom.now(),
      verifiedOn = None
    )

    for {
      _ <- repositories.users.create(createUserRow, "token")
    } yield createUserRow
  }
}

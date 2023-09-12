package utils

import net.wiringbits.common.models.Email
import net.wiringbits.core.RepositoryComponents
import net.wiringbits.typo_generated.customtypes.{TypoOffsetDateTime, TypoUUID, TypoUnknownCitext}
import net.wiringbits.typo_generated.public.users.{UsersId, UsersRow}

import scala.concurrent.{ExecutionContext, Future}

trait RepositoryUtils {
  def createNonVerifyUser(usersIdMaybe: Option[UsersId] = None, emailMaybe: Option[Email] = None)(using
      ec: ExecutionContext,
      repositories: RepositoryComponents
  ): Future[UsersRow] = {
    val usersId = usersIdMaybe.getOrElse(UsersId(TypoUUID.randomUUID))
    val email = emailMaybe.getOrElse(Email.trusted("hello@wiringbits.net"))

    val createUserRow = UsersRow(
      userId = usersId,
      name = "Sample",
      lastName = None,
      email = TypoUnknownCitext(email.string),
      password = "password",
      createdAt = TypoOffsetDateTime.now,
      verifiedOn = None
    )

    for {
      _ <- repositories.users.create(createUserRow, "token")
    } yield createUserRow
  }
}

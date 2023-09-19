package net.wiringbits.repositories

import net.wiringbits.common.models.UUIDCustom
import net.wiringbits.common.models.id.UserId
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensRepoImpl, UserTokensRow}
import play.api.db.Database

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class UserTokensRepository @Inject() (
    database: Database
)(implicit
    ec: DatabaseExecutionContext
) {

  def create(userTokensRow: UserTokensRow): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserTokensRepoImpl.insert(userTokensRow)
    }
  }

  def find(userId: UserId, token: String): Future[Option[UserTokensRow]] = Future {
    database.withConnection { implicit conn =>
      UserTokensRepoImpl.select
        .where(_.userId === userId)
        .where(_.token === token)
        .orderBy(_.createdAt.desc)
        .toList
        .headOption
    }
  }

  def find(userId: UserId): Future[List[UserTokensRow]] = Future {
    database.withConnection { implicit conn =>
      UserTokensRepoImpl.select.where(_.userId === userId).orderBy(_.createdAt.desc).toList
    }
  }

  def delete(userTokenId: UUIDCustom, userId: UserId): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserTokensRepoImpl.delete.where(_.userTokenId === userTokenId).where(_.userId === userId).execute()
    }
  }
}

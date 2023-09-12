package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.typo_generated.public.user_tokens.{UserTokensId, UserTokensRepoImpl, UserTokensRow}
import net.wiringbits.typo_generated.public.users.UsersId
import play.api.db.Database

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

  def find(usersId: UsersId, token: String): Future[Option[UserTokensRow]] = Future {
    database.withConnection { implicit conn =>
      UserTokensRepoImpl.select
        .where(_.userId === usersId)
        .where(_.token === token)
        .orderBy(_.createdAt.desc)
        .limit(1)
        .toList
        .headOption
    }
  }

  def find(usersId: UsersId): Future[List[UserTokensRow]] = Future {
    database.withConnection { implicit conn =>
      UserTokensRepoImpl.select.where(_.userId === usersId).orderBy(_.createdAt.asc).toList
    }
  }

  def delete(userTokenId: UserTokensId, usersId: UsersId): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserTokensRepoImpl.delete.where(_.userTokenId === userTokenId).where(_.userId === usersId).execute()
    }
  }
}

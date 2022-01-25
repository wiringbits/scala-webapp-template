package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.UserTokensDAO
import net.wiringbits.repositories.models.UserToken
import play.api.db.Database

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class UserTokensRepository @Inject() (
    database: Database
)(implicit
    ec: DatabaseExecutionContext
) {

  def create(request: UserToken.Create): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserTokensDAO.create(request)
    }
  }

  def find(userId: UUID, token: String): Future[Option[UserToken]] = Future {
    database.withConnection { implicit conn =>
      UserTokensDAO.find(userId, token)
    }
  }

  def find(userId: UUID): Future[List[UserToken]] = Future {
    database.withConnection { implicit conn =>
      UserTokensDAO.find(userId)
    }
  }

  def delete(tokenId: UUID, userId: UUID): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      UserTokensDAO.delete(tokenId, userId: UUID)
    }
  }
}

package net.wiringbits.repositories

import net.wiringbits.config.TokensConfig
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.TokensDAO
import net.wiringbits.repositories.models.Token
import play.api.db.Database

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class TokensRepository @Inject() (
    database: Database,
    tokensConfig: TokensConfig,
    clock: Clock
)(implicit
    ec: DatabaseExecutionContext
) {

  def create(request: Token.CreateToken): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      TokensDAO.create(request, tokensConfig.verificationTokenExp, clock)
    }
  }

  def find(userId: UUID, token: UUID): Future[Option[Token]] = Future {
    database.withConnection { implicit conn =>
      TokensDAO.find(userId, token)
    }
  }

  def find(userId: UUID): Future[List[Token]] = Future {
    database.withConnection { implicit conn =>
      TokensDAO.find(userId)
    }
  }

  def delete(tokenId: UUID): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      TokensDAO.delete(tokenId)
    }
  }
}

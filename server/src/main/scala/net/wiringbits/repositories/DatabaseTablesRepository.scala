package net.wiringbits.repositories

import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.DatabaseTablesDAO
import net.wiringbits.repositories.models.{ColumnMetadata, DatabaseTable, TableMetadata}
import play.api.db.Database

import javax.inject.Inject
import scala.concurrent.Future

class DatabaseTablesRepository @Inject() (database: Database)(implicit ec: DatabaseExecutionContext) {

  def all(): Future[List[DatabaseTable]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.all()
    }
  }

  def getTableMetadata(tableName: String): Future[TableMetadata] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.getTableMetadata(tableName)
    }
  }
}

package net.wiringbits.repositories

import net.wiringbits.config.models.DataExplorerSettings
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.DatabaseTablesDAO
import net.wiringbits.repositories.models.{DatabaseTable, TableMetadata}
import net.wiringbits.util.Pagination
import play.api.db.Database

import javax.inject.Inject
import scala.concurrent.Future

class DatabaseTablesRepository @Inject() (database: Database)(implicit
    ec: DatabaseExecutionContext,
    tableSettings: DataExplorerSettings
) {

  def all(): Future[List[DatabaseTable]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.all()
    }
  }

  def getSettingsTables(tableSettings: DataExplorerSettings): Future[List[DatabaseTable]] = Future {
    DatabaseTablesDAO.getSettingsTables(tableSettings)
  }

  def getTableMetadata(tableName: String, pagination: Pagination): Future[TableMetadata] = Future {
    database.withConnection { implicit conn =>
      val metadata = DatabaseTablesDAO.getTableMetadata(tableName)
      DatabaseTablesDAO.getTableData(tableName, metadata, pagination, tableSettings);
    }
  }
}

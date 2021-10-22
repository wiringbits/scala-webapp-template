package net.wiringbits.repositories

import net.wiringbits.config.models.DataExplorerSettings
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.DatabaseTablesDAO
import net.wiringbits.repositories.models.{DatabaseTable, TableField, TableMetadata, TableRow}
import net.wiringbits.util.models.pagination.{PaginatedQuery, PaginatedResult}
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

  def getTablesInSettings(tableSettings: DataExplorerSettings): Future[List[DatabaseTable]] = Future {
    DatabaseTablesDAO.getTablesInSettings(tableSettings)
  }

  def getTableFields(tableName: String): Future[List[TableField]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.getTableFields(tableName)
    }
  }

  def getObligatoryFields(tableName: String): Future[List[TableField]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.getObligatoryFields(tableName, tableSettings)
    }
  }

  def getTableMetadata(tableName: String, pagination: PaginatedQuery): Future[PaginatedResult[TableMetadata]] = Future {
    database.withConnection { implicit conn =>
      val fields = DatabaseTablesDAO.getTableFields(tableName)
      DatabaseTablesDAO.getTableData(tableName, fields, pagination, tableSettings);
    }
  }

  def find(tableName: String, ID: String): Future[TableRow] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.find(tableName, ID, tableSettings);
    }
  }

  def create(tableName: String, body: Map[String, String]): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.create(tableName, body, tableSettings);
    }
  }

  def update(tableName: String, ID: String, body: Map[String, String]): Future[Unit] =
    Future {
      database.withConnection { implicit conn =>
        DatabaseTablesDAO.update(tableName, ID, tableSettings, body);
      }
    }

  def delete(tableName: String, ID: String): Future[Unit] =
    Future {
      database.withConnection { implicit conn =>
        DatabaseTablesDAO.delete(tableName, ID, tableSettings);
      }
    }
}

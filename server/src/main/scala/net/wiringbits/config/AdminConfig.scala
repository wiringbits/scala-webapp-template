package net.wiringbits.config

import net.wiringbits.config.models.DataExplorerSettings
import net.wiringbits.repositories.daos.DatabaseTablesDAO
import net.wiringbits.repositories.models.{DatabaseTable, TableField}
import play.api.db.Database

import javax.inject.Inject

class AdminConfig @Inject() (
    database: Database,
    settings: DataExplorerSettings
) {
  start()

  def start(): Unit = {
    database.withConnection { implicit conn =>
      val tables = DatabaseTablesDAO.all()
      for (settingsTable <- settings.tables) {
        val fields = DatabaseTablesDAO.getTableFields(settingsTable.tableName)
        validateTableName(settingsTable.tableName, tables)
        validateOrderingCondition(settingsTable.defaultOrderByClause.string, fields)
        validateIDFieldName(settingsTable.IDFieldName, fields)
      }
    }
  }

  def validateTableName(tableName: String, tablesInDB: List[DatabaseTable]): Unit = {
    if (tablesInDB.exists(_.name == tableName)) ()
    else
      throw new RuntimeException(
        s"$tableName doesn't exists in DB: $tablesInDB"
      )
  }

  def validateOrderingCondition(orderingCondition: String, fields: List[TableField]): Unit = {
    // TODO: I don't think these validators are enough
    val splittedCondition = orderingCondition.split(",")

    val _ = for {
      orderSQL <- splittedCondition
      fieldName = orderSQL.toUpperCase.replaceAll("(ASC)|(DESC)", "").trim
      exists = fields.exists(_.name.toUpperCase == fieldName)
    } yield
      if (exists) () else throw new RuntimeException(s"You need to include a valid field name on OrderingCondition")
  }

  def validateIDFieldName(IDFieldName: String, fields: List[TableField]): Unit = {
    val exists = fields.exists(_.name == IDFieldName)
    if (exists) () else throw new RuntimeException(s"Value on IDFieldName on DataExplorer settings doesn't exists")
  }

}

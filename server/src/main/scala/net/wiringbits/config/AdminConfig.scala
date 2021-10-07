package net.wiringbits.config

import net.wiringbits.config.models.{DataExplorerSettings, TableSettings}
import net.wiringbits.repositories.daos.DatabaseTablesDAO
import net.wiringbits.repositories.models.DatabaseTable
import play.api.db.Database

import javax.inject.Inject

class AdminConfig @Inject() (
    database: Database,
    settings: DataExplorerSettings
) {
  start()

  def start(): Unit = {
    val tables = database.withConnection { implicit conn => DatabaseTablesDAO.allSQL() }
    val settingsTables = settings.tables

    for (settingsTable <- settingsTables) {
      validateTable(tables, settingsTable)
      validateOrderingCondition(settingsTable)
      // validateIDFieldName(settingsTable)
    }
  }

  def validateTable(tablesInDB: List[DatabaseTable], tableSettings: TableSettings): Unit = {
    val settingsTableName = tableSettings.tableName
    if (tablesInDB.exists(_.name == settingsTableName)) ()
    else
      throw new RuntimeException(
        s"$settingsTableName doesn't exists in DB: $tablesInDB"
      )

  }

  def validateOrderingCondition(tableSettings: TableSettings): Unit = {
    val orderingCondition = tableSettings.defaultOrderByClause.string
    if (orderingCondition.contains("DESC") || orderingCondition.contains("ASC")) ()
    else
      throw new RuntimeException(
        s"You need to include a DESC or ASC property on tableSettings"
      )

    // TODO: Validate that the field on orderingCondition exists
    // val fields = database.withConnection { implicit conn => DatabaseTablesDAO.getTableFields(tableSettings.name) }
  }

  /*
  def validateIDFieldName(settingsTable: TableSettings): Unit = {
    // TODO: Check if exists
  }
   */

}

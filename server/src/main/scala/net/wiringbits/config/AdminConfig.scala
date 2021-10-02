package net.wiringbits.config

import net.wiringbits.repositories.daos.DatabaseTablesDAO
import play.api.db.Database

import javax.inject.Inject

class AdminConfig @Inject() (database: Database) {
  start()

  def start(): Unit = {
    val tables = database.withConnection(implicit conn => DatabaseTablesDAO.all())

    val settingsTables = DatabaseTablesDAO.getSettingsTables(this)

    val _ = for {
      settingsTable <- settingsTables
      exists = tables.exists(_.name == settingsTable.name)
    } yield if (exists) () else throw new RuntimeException(s"${settingsTable.name} doesn't exists in DB: $tables")
  }

  val tables: List[TableSettings] = List(
    TableSettings("users", "created_at DESC, user_id")
  )
}
case class DataExplorerSettings(tables: List[TableSettings])
case class TableSettings(name: String, defaultOrderByClause: String)

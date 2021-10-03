package net.wiringbits.config

import net.wiringbits.config.models.DataExplorerSettings
import net.wiringbits.repositories.daos.DatabaseTablesDAO
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

    val _ = for {
      settingsTable <- settingsTables
      exists = tables.exists(_.name == settingsTable.name)
    } yield if (exists) () else throw new RuntimeException(s"${settingsTable.name} doesn't exists in DB: $tables")

  }
}

package net.wiringbits.config

<<<<<<< HEAD
import net.wiringbits.config.models.DataExplorerSettings
=======
>>>>>>> c9246b6641983e77d35d2fc256b80e269328ce68
import net.wiringbits.repositories.daos.DatabaseTablesDAO
import play.api.db.Database

import javax.inject.Inject

<<<<<<< HEAD
class AdminConfig @Inject() (
    database: Database,
    settings: DataExplorerSettings
) {
  start()

  def start(): Unit = {
    val tables = database.withConnection { implicit conn => DatabaseTablesDAO.allSQL() }
    val settingsTables = settings.tables
=======
class AdminConfig @Inject() (database: Database) {
  start()

  def start(): Unit = {
    val tables = database.withConnection(implicit conn => DatabaseTablesDAO.all())

    val settingsTables = DatabaseTablesDAO.getSettingsTables(this)
>>>>>>> c9246b6641983e77d35d2fc256b80e269328ce68

    val _ = for {
      settingsTable <- settingsTables
      exists = tables.exists(_.name == settingsTable.name)
    } yield if (exists) () else throw new RuntimeException(s"${settingsTable.name} doesn't exists in DB: $tables")
<<<<<<< HEAD

  }
}
=======
  }

  val tables: List[TableSettings] = List(
    TableSettings("users", "created_at DESC, user_id")
  )
}
case class DataExplorerSettings(tables: List[TableSettings])
case class TableSettings(name: String, defaultOrderByClause: String)
>>>>>>> c9246b6641983e77d35d2fc256b80e269328ce68

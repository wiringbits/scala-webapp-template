package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.webapp.utils.admin.config.{DataExplorerSettings, TableSettings}

class DataExplorerModule extends AbstractModule {

  @Provides()
  def dataExplorerSettings: DataExplorerSettings = DataExplorerSettings(baseUrl, settings)

  val baseUrl = net.wiringbits.BuildInfo.apiUrl.filter(_.nonEmpty).getOrElse {
    "http://localhost:9000"
  }

  val settings = List(
    TableSettings(
      tableName = "users",
      primaryKeyField = "user_id",
      hiddenColumns = List("password", "email"),
      // to prevent garbage data, let's disable most columns
      nonEditableColumns = List("user_id", "email", "created_at", "verified_on", "name"),
      canBeDeleted = false,
      filterableColumns = List("name", "last_name")
    )
  )
}

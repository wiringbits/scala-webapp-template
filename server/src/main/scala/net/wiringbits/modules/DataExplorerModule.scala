package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provides}

class DataExplorerModule extends AbstractModule {

  @Provides()
  def dataExplorerSettings: DataExplorerSettings = {
    DataExplorerSettings(settings)
  }

  val settings: List[TableSettings] = List(
    TableSettings("users", "created_at DESC, user_id")
  )
}

case class DataExplorerSettings(tables: List[TableSettings])
case class TableSettings(name: String, defaultOrderByClause: String)

package net.wiringbits.modules
import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.webapp.utils.admin.config.{DataExplorerSettings, TableSettings}

class DataExplorerModule extends AbstractModule {

  @Provides()
  def dataExplorerSettings: DataExplorerSettings = DataExplorerSettings(settings)

  val settings = List(
    TableSettings("users", "user_id")
  )
}

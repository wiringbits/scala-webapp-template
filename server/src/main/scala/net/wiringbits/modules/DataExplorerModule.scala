package net.wiringbits.modules
import net.wiringbits.webapp.utils.admin.config.TableSettings
import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings

class DataExplorerModule extends AbstractModule {

  @Provides()
  def dataExplorerSettings: DataExplorerSettings = DataExplorerSettings(settings)

  val settings: List[TableSettings] = List(
    TableSettings("users", "user_id")
  )
}

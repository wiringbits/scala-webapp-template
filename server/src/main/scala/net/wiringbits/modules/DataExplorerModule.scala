package net.wiringbits.modules
import net.wiringbits.webapp.utils.admin.config.TableSettings
import net.wiringbits.webapp.utils.admin.utils.models.ordering.OrderingCondition
import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings

class DataExplorerModule extends AbstractModule {

  @Provides()
  def dataExplorerSettings: DataExplorerSettings = DataExplorerSettings(settings)

  val settings = List(
    TableSettings("users", OrderingCondition("created_at DESC, user_id"), "user_id")
  )
}

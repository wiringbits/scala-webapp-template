package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.api.models.AdminGetTablesResponse
import net.wiringbits.ui.components.core.RemoteDataLoader
import net.wiringbits.{API, AppStrings}
import net.wiringbits.ui.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.ui.core.GenericHooks
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object Tables {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (timesRefreshingData, forceRefresh) = GenericHooks.useForceRefresh

    RemoteDataLoader.component[AdminGetTablesResponse](
      RemoteDataLoader
        .Props(
          fetch = () => props.api.client.adminGetTables(),
          render = response => TableList.component(TableList.Props(response, forceRefresh)),
          progressIndicator = () => loader,
          watchedObjects = List(timesRefreshingData)
        )
    )
  }

  private def loader = Container(
    flex = Some(1),
    alignItems = Container.Alignment.center,
    justifyContent = Container.Alignment.center,
    child = Fragment(
      CircularLoader(),
      mui.Typography(AppStrings.loading).variant(muiStrings.h6)
    )
  )

}

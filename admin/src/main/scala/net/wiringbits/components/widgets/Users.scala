package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{StyleRulesCallback, Styles, WithStylesOptions}
import net.wiringbits.api.models.AdminGetUsersResponse
import net.wiringbits.ui.components.core.RemoteDataLoader
import net.wiringbits.ui.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.ui.core.GenericHooks
import net.wiringbits.{API, AppStrings}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object Users {
  case class Props(api: API)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme => StringDictionary()
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (timesRefreshingData, forceRefresh) = GenericHooks.useForceRefresh

    RemoteDataLoader.component[AdminGetUsersResponse](
      RemoteDataLoader
        .Props(
          fetch = () => props.api.client.adminGetUsers(),
          render = response => UserList.component(UserList.Props(response, forceRefresh)),
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

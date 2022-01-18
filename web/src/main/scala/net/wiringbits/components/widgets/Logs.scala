package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.models.User
import net.wiringbits.ui.components.core.RemoteDataLoader
import net.wiringbits.ui.components.core.widgets._
import net.wiringbits.ui.core.GenericHooks
import net.wiringbits.{API, AppStrings}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object Logs {
  case class Props(api: API, user: User)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "list" -> CSSProperties()
          .setWidth("100%")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (timesRefreshingData, forceRefresh) = GenericHooks.useForceRefresh

    RemoteDataLoader.component[GetUserLogs.Response](
      RemoteDataLoader.Props(
        fetch = () => props.api.client.getUserLogs(props.user.jwt),
        render = response => LogList.component(LogList.Props(response, () => forceRefresh())),
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

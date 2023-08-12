package net.wiringbits

import com.alexitc.materialui.facade.materialUiCore.components as mui
import com.alexitc.materialui.facade.materialUiStyles.components.ThemeProvider
import net.wiringbits.components.AppSplash
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.ReactElement
import typings.reactRouterDom.components as router

object App {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ThemeProvider(AppTheme.value)(
      mui.MuiThemeProvider(AppTheme.value)(
        mui.CssBaseline(),
        router.BrowserRouter.basename("")(
          AppSplash(props.ctx)(AppRouter(props.ctx): ReactElement)
        )
      )
    )
  }
}

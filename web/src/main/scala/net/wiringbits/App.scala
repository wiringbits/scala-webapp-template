package net.wiringbits

import com.olvind.mui.muiMaterial.components.ThemeProvider
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.components.AppSplash
import net.wiringbits.core.ReactiveHooks
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.BrowserRouter
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.ReactElement

object App {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val isDark = ReactiveHooks.useDistinctValue(props.ctx.$darkMode)
    val theme = if (isDark) AppTheme.darkTheme else AppTheme.light

    ThemeProvider(theme)(
      mui.ThemeProvider(theme)(
        mui.CssBaseline(),
        BrowserRouter(basename = "")(
          AppSplash(props.ctx)(AppRouter(props.ctx): ReactElement)
        )
      )
    )
  }
}

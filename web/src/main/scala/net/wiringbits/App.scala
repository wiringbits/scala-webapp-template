package net.wiringbits

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.components.ThemeProvider
import net.wiringbits.components.AppSplash
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.BrowserRouter
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object App {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val appRouter = AppRouter(props.ctx)

    ThemeProvider(AppTheme.value)(
      mui.MuiThemeProvider(AppTheme.value)(
        mui.CssBaseline(),
        BrowserRouter(basename = "/")(
          AppSplash(props.ctx, appRouter)
        )
      )
    )
  }
}

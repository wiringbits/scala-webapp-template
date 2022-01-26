package net.wiringbits

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.components.ThemeProvider
import net.wiringbits.components.AppSplash
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import typings.reactRouterDom.{components => router}

@react object App {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val appRouter = AppRouter(props.ctx)

    ThemeProvider(AppTheme.value)(
      mui.MuiThemeProvider(AppTheme.value)(
        mui.CssBaseline(),
        router.BrowserRouter.basename("")(
          AppSplash(props.ctx, appRouter)
        )
      )
    )
  }
}

package net.wiringbits

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.components.ThemeProvider
import slinky.core._
import slinky.core.annotations.react
import typings.reactRouterDom.{components => router}

@react object App {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ThemeProvider(AppTheme.value)(
      mui.MuiThemeProvider(AppTheme.value)(
        mui.CssBaseline(),
        router.BrowserRouter.basename("")(
          AppRouter(props.api)
        )
      )
    )
  }
}

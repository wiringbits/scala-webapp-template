package net.wiringbits

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.components.ThemeProvider
import net.wiringbits.components.AppSplash
import net.wiringbits.models.{AuthState, User}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import typings.reactRouterDom.{components => router}

@react object App {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (auth, setAuth) = Hooks.useState[AuthState](AuthState.Unauthenticated)

    def loggedIn(user: User): Unit = {
      props.api.storage.saveJwt(user.jwt)
      setAuth(AuthState.Authenticated(user))
    }

    def loggedOut(): Unit = {
      props.api.storage.saveJwt("")
      setAuth(AuthState.Unauthenticated)
    }

    val appRouter = AppRouter(props.api, auth, loggedIn, () => loggedOut())

    ThemeProvider(AppTheme.value)(
      mui.MuiThemeProvider(AppTheme.value)(
        mui.CssBaseline(),
        router.BrowserRouter.basename("")(
          AppSplash(props.api, loggedIn, appRouter)
        )
      )
    )
  }
}

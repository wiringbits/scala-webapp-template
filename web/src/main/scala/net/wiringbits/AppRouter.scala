package net.wiringbits

import net.wiringbits.components.pages._
import net.wiringbits.components.widgets.{AppBar, Footer}
import net.wiringbits.models.{AuthState, User}
import net.wiringbits.ui.components.core.widgets
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.components.Route
import typings.reactRouterDom.{components => router}

@react object AppRouter {
  case class Props(api: API, auth: AuthState, loggedIn: User => Unit, logout: () => Unit)

  private def route(path: String, auth: AuthState)(child: => ReactElement): Route.Builder[RouteProps] = {
    router.Route(
      RouteProps()
        .setExact(true)
        .setPath(path)
        .setRender { route =>
          widgets.Scaffold(
            appbar = Some(AppBar(auth)),
            body = child,
            footer = Some(Footer())
          )
        }
    )
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val home = route("/", props.auth)(HomePage())
    val about = route("/about", props.auth)(AboutPage())
    val signIn = route("/signin", props.auth)(SignInPage(props.api, props.loggedIn))
    val signUp = route("/signup", props.auth)(SignUpPage(props.api, props.loggedIn))

    def dashboard(user: User) = route("/dashboard", props.auth)(DashboardPage(props.api, user))
    val signOut = route("/signout", props.auth) {
      props.logout()
      router.Redirect("/")
    }

    val catchAllRoute = router.Route(
      RouteProps().setRender { _ =>
        router.Redirect("/")
      }
    )

    props.auth match {
      case AuthState.Unauthenticated =>
        router.Switch(home, about, signIn, signUp, catchAllRoute)

      case AuthState.Authenticated(user) =>
        router.Switch(home, dashboard(user), about, signOut, catchAllRoute)
    }
  }
}

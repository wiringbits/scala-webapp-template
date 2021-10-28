package net.wiringbits

import net.wiringbits.components.pages.{HomePage, UsersPage}
import net.wiringbits.components.widgets.{AppBar, Footer}
import net.wiringbits.ui.components.core.widgets.Scaffold
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.components.Route
import typings.reactRouterDom.{components => router}

@react object AppRouter {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
//    def generateRoute(path: String, child: => ReactElement): Route.Builder[RouteProps] = {
//      router.Route(
//        RouteProps()
//          .setExact(true)
//          .setPath(path)
//          .setRender { route =>
//            Scaffold(appbar = Some(AppBar()), body = child, footer = Some(Footer()))
//          }
//      )
//    }
//
//    val home = generateRoute("/", HomePage())
//    val dashboard = generateRoute("/users", UsersPage(props.api))
//    val catchAllRoute = router.Route(
//      RouteProps().setRender { _ =>
//        router.Redirect("/")
//      }
//    )
//
//    router.BrowserRouter(
//      router.Switch(home, dashboard, catchAllRoute)
//    )
    HomePage()
  }
}

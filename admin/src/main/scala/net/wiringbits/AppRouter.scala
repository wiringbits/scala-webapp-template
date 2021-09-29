package net.wiringbits

import net.wiringbits.components.pages.{DataExplorerPage, HomePage, ExperimentalTablesPage, UsersPage}
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
    def generateRoute(path: String, child: => ReactElement): Route.Builder[RouteProps] = {
      router.Route(
        RouteProps()
          .setExact(true)
          .setPath(path)
          .setRender { route =>
            Scaffold(appbar = Some(AppBar()), body = child, footer = Some(Footer()))
          }
      )
    }

    val home = generateRoute("/", HomePage())
    val dashboard = generateRoute("/users", UsersPage(props.api))
    val dataExplorerPage = generateRoute("/tables", DataExplorerPage(props.api))
    val tablePage = generateRoute("/tables/:tableName", ExperimentalTablesPage(props.api))
    val catchAllRoute = router.Route(
      RouteProps().setRender { _ =>
        router.Redirect("/")
      }
    )

    router.Switch(home, dashboard, dataExplorerPage, tablePage, catchAllRoute)
  }
}

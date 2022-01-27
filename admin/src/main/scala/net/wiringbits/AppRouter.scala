package net.wiringbits

import net.wiringbits.components.pages.{HomePage, UsersPage}
import net.wiringbits.components.widgets.{AppBar, Footer}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Scaffold
import net.wiringbits.webapp.utils.ui.web.components.pages.{
  DataExplorerPage,
  RowViewPage,
  TableMetadataPage,
  UpdateRowPage
}
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
    val tables = generateRoute("/tables", DataExplorerPage.component(DataExplorerPage.Props(props.api.admin)))
    val dataExplorer = generateRoute(
      "/tables/:tableName",
      TableMetadataPage.component(TableMetadataPage.Props(props.api.admin))
    )
    val itemExplorer =
      generateRoute("/tables/:tableName/view/:ID", RowViewPage.component(RowViewPage.Props(props.api.admin)))
    val updateItem =
      generateRoute("/tables/:tableName/update/:ID", UpdateRowPage.component(UpdateRowPage.Props(props.api.admin)))
    val catchAllRoute = router.Route(
      RouteProps().setRender { _ =>
        router.Redirect("/")
      }
    )

    router.Switch(home, dashboard, tables, dataExplorer, itemExplorer, updateItem, catchAllRoute)
  }
}

package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.AppStrings
import net.wiringbits.api.models.AdminGetTablesResponse
import net.wiringbits.ui.components.core.widgets.{Container, Subtitle}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html.className
import typings.reactRouterDom.{components => router}

object TableList {
  case class Props(response: AdminGetTablesResponse, forceRefresh: () => Unit)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "tablesList" -> CSSProperties()
          .setWidth("100%"),
        "tableItem" -> CSSProperties()
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val pathname = dom.window.location.pathname
    val (routeValue, setRoute) = Hooks.useState(pathname)

    def goTo(table_name: String): Unit = {
      val url = s"/tables/$table_name"
      dom.window.location.href = url
    }

    val items = props.response.data.map { item =>
      mui
        .ListItem()(
          mui
            .Typography(className := classes("tableItem"))(item.table_name)
        )
        .button(true)
        .divider(true)
        .dense(true)
        .onClick(_ => goTo(item.table_name))
        .withKey(item.table_name)
    }

    Container(
      minWidth = Some("100%"),
      child = Fragment(
        Container(
          minWidth = Some("100%"),
          flexDirection = Container.FlexDirection.row,
          alignItems = Container.Alignment.center,
          justifyContent = Container.Alignment.spaceBetween,
          child = Fragment(
            Subtitle(AppStrings.tables),
            mui
              .Button("Reload")
              .color(Color.primary)
          )
        ),
        mui
          .List(items)
          .className(classes("tablesList"))
          .dense(true)
      )
    )

  }
}

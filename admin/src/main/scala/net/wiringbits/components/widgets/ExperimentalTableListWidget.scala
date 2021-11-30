package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
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
import net.wiringbits.util.formatField
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.facade.Fragment
import slinky.web.html.className
import typings.reactRouterDom.{mod => reactRouterDom}

object ExperimentalTableListWidget {
  case class Props(response: AdminGetTablesResponse)

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
    val history = reactRouterDom.useHistory()

    def goTo(tableName: String): Unit = {
      val url = s"/tables/$tableName"
      history.push(url)
    }

    val items = props.response.data.map { table =>
      mui
        .ListItem()(
          mui
            .Typography(className := classes("tableItem"))(formatField(table.name))
        )
        .button(true)
        .divider(true)
        .dense(true)
        .onClick(_ => goTo(table.name))
        .withKey(table.name)
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
            Subtitle(AppStrings.tables)
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

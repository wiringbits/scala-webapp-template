package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.AppStrings
import net.wiringbits.api.models.GetUserLogsResponse
import net.wiringbits.api.utils.Formatter
import net.wiringbits.ui.components.core.widgets._
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.facade.Fragment

object LogList {
  case class Props(response: GetUserLogsResponse, forceRefresh: () => Unit)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "list" -> CSSProperties()
          .setWidth("100%")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val items = props.response.data.map { item =>
      mui
        .ListItem(
          mui
            .ListItemText()
            .primary(item.message)
            .secondary(Formatter.instant(item.createdAt))
        )
        .divider(true)
        .withKey(item.id.toString)
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
            Subtitle(AppStrings.logs),
            mui
              .Button("Reload")
              .color(Color.primary)
              .onClick(_ => props.forceRefresh())
          )
        ),
        mui
          .List(items)
          .className(classes("list"))
          .dense(true)
      )
    )
  }
}

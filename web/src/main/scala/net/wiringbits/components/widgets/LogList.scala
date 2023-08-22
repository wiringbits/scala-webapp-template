package net.wiringbits.components.widgets

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.{components => mui}
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.muiMaterial.mod.PropTypes.Color

import net.wiringbits.AppContext
import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.api.utils.Formatter
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.Fragment

object LogList {
  def apply(ctx: AppContext, response: GetUserLogs.Response, forceRefresh: () => Unit): KeyAddingStage =
    component(Props(ctx = ctx, response = response, forceRefresh = forceRefresh))

  case class Props(ctx: AppContext, response: GetUserLogs.Response, forceRefresh: () => Unit)
  val styling = new CSSProperties {
    width = "100%"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val items = props.response.data.map { item =>
      mui.ListItem
        .normal()(
          mui
            .ListItemText()
            .primary(item.message)
            .secondary(Formatter.instant(item.createdAt))
        )
        .divider(true)
        .withKey(item.id.toString)
        .build
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
            Subtitle(texts.logs),
            mui.Button
              .normal()(texts.reload)
              .color(Color.primary)
              .onClick(_ => props.forceRefresh())
          )
        ),
        mui
          .List(items)
          .className("list")
          .style(styling)
          .dense(true)
      )
    )
  }
}

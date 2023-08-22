package net.wiringbits.components.widgets

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.{components => mui}
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
import com.olvind.mui.muiIconsMaterial.{components => muiIcons}

import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.EdgeInsets
import net.wiringbits.webapp.utils.slinkyUtils.core.MediaQueryHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.Fragment

object Footer {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)
  val styling = new CSSProperties {
    color = "#FFF"
    backgroundColor = "#222"
    borderRadius = 0
  }

  private val margin = 16

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val isMobileOrTablet = MediaQueryHooks.useIsMobileOrTablet()

    val appName = Container(
      margin = EdgeInsets.bottom(margin),
      child = mui.Typography(texts.appName).variant("h4").color(Color.inherit)
    )
    val appDescription =
      mui.Typography(texts.description).variant("body2").color(Color.inherit)

    def title(text: String) =
      Container(
        margin = EdgeInsets.bottom(margin),
        child = mui.Typography(text).variant("h5").color(Color.inherit)
      )

    def subtitle(text: String) =
      mui.Typography(text).variant("subtitle2").color(Color.inherit)

    def link(text: String, url: String) =
      mui
        .Link(
          mui.Typography(text).variant("body2").color(Color.inherit)
        )
        .href(url)
        .color(Color.inherit)

    val copyright = Container(
      margin = Container.EdgeInsets.vertical(margin),
      alignItems = Container.Alignment.center,
      child = mui.Typography(texts.appNameCopyright).color(Color.inherit)
    )

    val projects = Container(
      flex = Some(1),
      child = Fragment(
        title("Projects"),
        link("CollabUML", "https://collabuml.com"),
        link("The Stakenet Block Explorer", "https://xsnexplorer.io/"),
        link("The Stakenet Orderbook", "https://orderbook.stakenet.io/XSN_BTC"),
        link("Pull Request Attention", "https://prattention.com"),
        link("CazaDescuentos", "https://cazadescuentos.net"),
        link("safer.chat", "https://safer.chat"),
        link("Crypto Coin Alerts", "https://github.com/AlexITC/crypto-coin-alerts")
      )
    )

    val contact = Container(
      flex = Some(1),
      child = Fragment(
        title(texts.contact),
        Container(
          child = Fragment(
            subtitle(texts.contact),
            link(props.ctx.contactEmail.string, s"mailto:${props.ctx.contactEmail.string}")
          )
        ),
        Container(
          margin = Container.EdgeInsets.top(margin / 2),
          child = Fragment(
            subtitle(texts.phone),
            mui.Typography(props.ctx.contactPhone).variant("body2").color(Color.inherit)
          )
        )
      )
    )

    val body = if (isMobileOrTablet) {
      Container(
        padding = Container.EdgeInsets.all(margin),
        child = Fragment(
          appName,
          appDescription,
          Container(margin = Container.EdgeInsets.top(margin), child = projects),
          Container(margin = Container.EdgeInsets.top(margin), child = contact)
        )
      )
    } else {

      Container(
        padding = Container.EdgeInsets.all(margin),
        flexDirection = Container.FlexDirection.row,
        child = Fragment(
          Container(
            margin = Container.EdgeInsets.right(margin / 2),
            flex = Some(1),
            child = Fragment(appName, appDescription)
          ),
          Container(
            flex = Some(1),
            margin = Container.EdgeInsets.left(margin / 2),
            flexDirection = Container.FlexDirection.row,
            child = Fragment(
              projects,
              contact
            )
          )
        )
      )
    }

    mui
      .Paper()(
        Fragment(
          body,
          copyright
        )
      )
      .component("footer")
      .className("footer")
      .style(styling)
  }
}

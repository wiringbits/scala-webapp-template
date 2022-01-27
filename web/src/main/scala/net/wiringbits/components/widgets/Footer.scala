package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{typographyTypographyMod, components => mui}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.EdgeInsets
import net.wiringbits.webapp.utils.slinkyUtils.core.MediaQueryHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object Footer {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "footer" -> CSSProperties()
          .setColor("#FFF")
          .setBackgroundColor("#222")
          .setBorderRadius(0)
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  private val margin = 16

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val classes = useStyles(())
    val isMobileOrTablet = MediaQueryHooks.useIsMobileOrTablet()

    val appName = Container(
      margin = EdgeInsets.bottom(margin),
      child = mui.Typography(texts.appName).variant(typographyTypographyMod.Style.h4).color(Color.inherit)
    )
    val appDescription =
      mui.Typography(texts.description).variant(typographyTypographyMod.Style.body2).color(Color.inherit)

    def title(text: String) =
      Container(
        margin = EdgeInsets.bottom(margin),
        child = mui.Typography(text).variant(typographyTypographyMod.Style.h5).color(Color.inherit)
      )

    def subtitle(text: String) =
      mui.Typography(text).variant(typographyTypographyMod.Style.subtitle2).color(Color.inherit)

    def link(text: String, url: String) =
      mui
        .Link(
          mui.Typography(text).variant(typographyTypographyMod.Style.body2).color(Color.inherit)
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
            mui.Typography(props.ctx.contactPhone).variant(typographyTypographyMod.Style.body2).color(Color.inherit)
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
      .className(classes("footer"))
  }
}

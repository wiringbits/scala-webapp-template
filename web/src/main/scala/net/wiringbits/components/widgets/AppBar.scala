package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, TextAlignProperty}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.AppContext
import net.wiringbits.core.{I18nHooks, ReactiveHooks}
import net.wiringbits.models.AuthState
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, NavLinkButton, Subtitle, Title}
import net.wiringbits.webapp.utils.slinkyUtils.core.MediaQueryHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html._

@react object AppBar {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "appbar" -> CSSProperties()
          .setColor("#FFF"),
        "toolbar" -> CSSProperties()
          .setDisplay("flex")
          .setAlignItems("center")
          .setJustifyContent("space-between"),
        "toolbar-mobile" -> CSSProperties()
          .setDisplay("flex")
          .setAlignItems("center"),
        "menu" -> CSSProperties()
          .setDisplay("flex"),
        "menu-mobile" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setColor("#222")
          .setTextAlign(TextAlignProperty.right)
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
    val classes = useStyles(())
    val isMobileOrTablet = MediaQueryHooks.useIsMobileOrTablet()
    val (visibleDrawer, setVisibleDrawer) = Hooks.useState(false)

    def onButtonClick(): Unit = {
      if (visibleDrawer) {
        setVisibleDrawer(false)
      }
    }

    val menu = auth match {
      case AuthState.Authenticated(_) =>
        Fragment(
          NavLinkButton("/", texts.home, onButtonClick),
          NavLinkButton("/dashboard", texts.dashboard, onButtonClick),
          NavLinkButton("/about", texts.about, onButtonClick),
          NavLinkButton("/me", texts.profile, onButtonClick),
          NavLinkButton("/signout", texts.signOut, onButtonClick)
        )

      case AuthState.Unauthenticated =>
        Fragment(
          NavLinkButton("/", texts.home, onButtonClick),
          NavLinkButton("/about", texts.about, onButtonClick),
          NavLinkButton("/signup", texts.signUp, onButtonClick),
          NavLinkButton("/signin", texts.signIn, onButtonClick)
        )
    }

    if (isMobileOrTablet) {
      val drawerContent = Container(
        minWidth = Some("256px"),
        flex = Some(1),
        margin = EdgeInsets.bottom(32),
        alignItems = Alignment.flexEnd,
        justifyContent = Alignment.spaceBetween,
        child = Fragment(
          mui
            .AppBar(className := classes("appbar"))
            .position(muiStrings.relative)(
              mui.Toolbar(className := classes("toolbar-mobile"))(
                Subtitle(texts.appName)
              )
            ),
          Container(
            alignItems = Alignment.flexEnd,
            justifyContent = Alignment.spaceBetween,
            child = menu
          )
        )
      )

      val drawer = mui.SwipeableDrawer(
        open = visibleDrawer,
        onOpen = _ => setVisibleDrawer(true),
        onClose = _ => setVisibleDrawer(false)
      )(drawerContent)

      val toolbar = mui.Toolbar(className := classes("toolbar-mobile"))(
        mui
          .IconButton(mui.Icon(muiIcons.Menu()))
          .color(Color.inherit)
          .onClick(_ => setVisibleDrawer(true)),
        Subtitle(texts.appName)
      )

      mui
        .AppBar(className := classes("appbar"))
        .position(muiStrings.relative)(toolbar, drawer)
    } else {
      mui
        .AppBar(className := classes("appbar"))
        .position(muiStrings.relative)(
          mui.Toolbar(className := classes("toolbar"))(
            Title(texts.appName),
            div(className := classes("menu"))(menu)
          )
        )
    }
  }
}

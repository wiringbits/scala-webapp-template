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
import net.wiringbits.AppStrings
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
  case class Props(auth: AuthState)

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
    val classes = useStyles(())
    val isMobileOrTablet = MediaQueryHooks.useIsMobileOrTablet()
    val (visibleDrawer, setVisibleDrawer) = Hooks.useState(false)

    def onButtonClick(): Unit = {
      if (visibleDrawer) {
        setVisibleDrawer(false)
      }
    }

    val menu = props.auth match {
      case AuthState.Authenticated(_) =>
        Fragment(
          NavLinkButton("/", AppStrings.home, onButtonClick),
          NavLinkButton("/dashboard", AppStrings.dashboard, onButtonClick),
          NavLinkButton("/about", AppStrings.about, onButtonClick),
          NavLinkButton("/me", AppStrings.me, onButtonClick),
          NavLinkButton("/signout", AppStrings.signOut, onButtonClick)
        )

      case AuthState.Unauthenticated =>
        Fragment(
          NavLinkButton("/", AppStrings.home, onButtonClick),
          NavLinkButton("/about", AppStrings.about, onButtonClick),
          NavLinkButton("/signup", AppStrings.signUp, onButtonClick),
          NavLinkButton("/signin", AppStrings.signIn, onButtonClick)
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
                Subtitle(AppStrings.appName)
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
        Subtitle(AppStrings.appName)
      )

      mui
        .AppBar(className := classes("appbar"))
        .position(muiStrings.relative)(toolbar, drawer)
    } else {
      mui
        .AppBar(className := classes("appbar"))
        .position(muiStrings.relative)(
          mui.Toolbar(className := classes("toolbar"))(
            Title(AppStrings.appName),
            div(className := classes("menu"))(menu)
          )
        )
    }
  }
}

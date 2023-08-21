package net.wiringbits.components.widgets

import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.csstype.mod.Property.{FlexDirection, TextAlign}
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
import com.olvind.mui.muiIconsMaterial.components as muiIcons
import net.wiringbits.AppContext
import net.wiringbits.core.{I18nHooks, ReactiveHooks}
import net.wiringbits.models.AuthState
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, NavLinkButton, Subtitle, Title}
import net.wiringbits.webapp.utils.slinkyUtils.core.MediaQueryHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.{Fragment, Hooks, ReactElement}
import slinky.web.html.*

object AppBar {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx))
    
  case class Props(ctx: AppContext)

  val appbarStyling=new CSSProperties {
     color="#FFF"
  } 
  val toolBarStyling=new CSSProperties {
     display="flex"
     alignItems="center"
     justifyContent="space-between"
  }  
  val toolbarMobileStyling=new CSSProperties {
     display="flex"
     alignItems="center"
  }  
  val menuStyling=new CSSProperties {
     display="flex"
  }
  val menuMobile =  new CSSProperties{
     display="flex"
     flexDirection=FlexDirection.column
     color="#222"
     textAlign=TextAlign.right
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
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
            .AppBar(className := "appbar",style:=appbarStyling)
            .position("relative")(
              mui.Toolbar(className := "toolbar-mobile",style:=toolbarMobileStyling)(
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
        onOpen = _ => setVisibleDrawer(true),
        onClose = _ => setVisibleDrawer(false)
      )(drawerContent).open(visibleDrawer)

      val toolbar = mui.Toolbar(className := "toolbar-mobile",style:=toolbarMobileStyling)(
        mui
          .IconButton.normal()(mui.Icon(muiIcons.Menu()))
          .color(Color.inherit)
          .onClick(_ => setVisibleDrawer(true)),
        Subtitle(texts.appName)
      )

      mui
        .AppBar(className := "appbar",style:=appbarStyling)
        .position("relative")(toolbar, drawer)
    } else {
      mui
        .AppBar(className := "appbar",style:=appbarStyling)
        .position("relative")(
          mui.Toolbar(className := "toolbar",style:=toolBarStyling)(
            Title(texts.appName),
            div(className := "menu",style:=menuStyling)(menu)
          )
        )
    }
  }
}

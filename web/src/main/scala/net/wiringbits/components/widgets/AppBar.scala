package net.wiringbits.components.widgets

import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.{I18nHooks, ReactiveHooks}
import net.wiringbits.models.AuthState
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.core.MediaQueryHooks
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.{useHistory, useLocation}
import slinky.core.facade.{Fragment, Hooks, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.*

import scala.scalajs.js

object AppBar {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx))

  case class Props(ctx: AppContext)

  // Filled (primary) app bar styles
  private val appBarSx = js.Dynamic.literal(
    background = "#0E8C6F",
    color = "#FFFFFF",
    borderBottom = "none",
    boxShadow = "0 1px 0 rgba(0,0,0,0.04), 0 8px 24px rgba(0,0,0,0.06)",
    backdropFilter = "saturate(180%) blur(8px)"
  )

  private val toolbarSx = js.Dynamic.literal(
    minHeight = 66,
    display = "flex",
    alignItems = "center"
  )

  private val brandSx = js.Dynamic.literal(
    display = "flex",
    alignItems = "center",
    gap = "12px",
    cursor = "pointer",
    marginRight = 16
  )

  private val logoSx = new CSSPropertiesUtils {
    height = "34px"
    width = "auto"
    display = "block"
  }

  private val dividerSx = new CSSPropertiesUtils {
    width = "1px"
    height = "26px"
    background = "rgba(255,255,255,0.32)"
    flexShrink = 0
    display = "block"
  }

  private val brandTextSx = js.Dynamic.literal(
    display = "block",
    fontWeight = 600,
    fontSize = 13.5,
    letterSpacing = "0.01em",
    color = "rgba(255,255,255,0.82)",
    whiteSpace = "nowrap"
  )

  // Active nav link: underline indicator via ::after
  private val navLinkActiveSx: js.Object = {
    val obj = js.Dynamic.literal(
      color = "#FFFFFF",
      fontWeight = 700,
      fontSize = 14.5,
      px = 1.5,
      minWidth = 0,
      position = "relative",
      borderRadius = 1.5
    )
    obj.updateDynamic("&::after")(
      js.Dynamic.literal(
        content = "\"\"",
        position = "absolute",
        left = 12,
        right = 12,
        bottom = 4,
        height = 2,
        borderRadius = 2,
        background = "#FFFFFF"
      )
    )
    obj.updateDynamic("&:hover")(
      js.Dynamic.literal(
        background = "rgba(255,255,255,0.12)",
        color = "#FFFFFF"
      )
    )
    obj.asInstanceOf[js.Object]
  }

  // Inactive nav link
  private val navLinkSx: js.Object = {
    val obj = js.Dynamic.literal(
      color = "rgba(255,255,255,0.86)",
      fontWeight = 600,
      fontSize = 14.5,
      px = 1.5,
      minWidth = 0,
      position = "relative",
      borderRadius = 1.5
    )
    obj.updateDynamic("&:hover")(
      js.Dynamic.literal(
        background = "rgba(255,255,255,0.12)",
        color = "#FFFFFF"
      )
    )
    obj.asInstanceOf[js.Object]
  }

  // "Sign in" — text button, white/dim
  private val signInBtnSx = js.Dynamic.literal(
    color = "rgba(255,255,255,0.92)",
    fontWeight = 600
  )

  // "Sign up" — white contained, primary text
  private val signUpBtnSx = js.Dynamic.literal(
    marginLeft = 8,
    backgroundColor = "#FFFFFF",
    color = "#0E8C6F",
    fontWeight = 700
  )

  // "Sign out" — outlined-like text button with manual border
  private val signOutBtnSx: js.Object = {
    val obj = js.Dynamic.literal(
      marginLeft = 4,
      color = "#FFFFFF",
      border = "1px solid rgba(255,255,255,0.4)",
      fontWeight = 600
    )
    obj.updateDynamic("&:hover")(
      js.Dynamic.literal(
        border = "1px solid #FFFFFF",
        background = "rgba(255,255,255,0.10)"
      )
    )
    obj.asInstanceOf[js.Object]
  }

  private val menuIconSx = new CSSPropertiesUtils {
    color = "#FFFFFF"
  }

  private val drawerMenuSx = js.Dynamic.literal(
    minWidth = 200
  )

  private val mobileMenuItemSx = js.Dynamic.literal(
    fontWeight = 500,
    color = "text.primary",
    gap = "12px",
    paddingTop = "9px",
    paddingBottom = "9px"
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
    val isDark = ReactiveHooks.useDistinctValue(props.ctx.$darkMode)
    val history = useHistory()
    val location = useLocation()
    val pathname = location.asInstanceOf[js.Dynamic].pathname.asInstanceOf[String]
    val isMobile = MediaQueryHooks.useIsMobileOrTablet()
    val (drawerOpen, setDrawerOpen) = Hooks.useState(false)

    val logo = img(
      src := "/img/wiringbits-logo-reverse-rgb.svg",
      alt := "wiringbits",
      style := logoSx
    )

    val brand: ReactElement = mui
      .Box()
      .sx(brandSx.asInstanceOf[CSSPropertiesUtils])
      .onClick(_ => history.push("/")) {
        Fragment(
          logo,
          span(style := dividerSx),
          mui
            .Typography("Web App Template")
            .sx(brandTextSx.asInstanceOf[CSSPropertiesUtils])
            .component("span")
        )
      }

    def navBtn(label: String, path: String, active: Boolean): ReactElement =
      mui.Button
        .normal()(label)
        .sx((if (active) navLinkActiveSx else navLinkSx).asInstanceOf[CSSPropertiesUtils])
        .onClick(_ => history.push(path))

    val authBtns: ReactElement = auth match {
      case AuthState.Authenticated(_) =>
        mui.Button
          .normal()(mui.Icon(muiIcons.Logout()), " ", texts.signOut)
          .sx(signOutBtnSx.asInstanceOf[CSSPropertiesUtils])
          .size("small")
          .onClick(_ => history.push("/signout"))

      case AuthState.Unauthenticated =>
        Fragment(
          mui.Button
            .normal()(texts.signIn)
            .sx(signInBtnSx.asInstanceOf[CSSPropertiesUtils])
            .variant("text")
            .size("small")
            .onClick(_ => history.push("/signin")),
          mui.Button
            .normal()(texts.signUp)
            .sx(signUpBtnSx.asInstanceOf[CSSPropertiesUtils])
            .variant("contained")
            .size("small")
            .onClick(_ => history.push("/signup"))
        )
    }

    val navLinks: ReactElement = auth match {
      case AuthState.Authenticated(_) =>
        Fragment(
          navBtn(texts.home, "/", pathname == "/"),
          navBtn(texts.dashboard, "/dashboard", pathname == "/dashboard"),
          navBtn(texts.about, "/about", pathname == "/about"),
          navBtn(texts.profile, "/me", pathname == "/me")
        )
      case AuthState.Unauthenticated =>
        Fragment(
          navBtn(texts.home, "/", pathname == "/"),
          navBtn(texts.about, "/about", pathname == "/about")
        )
    }

    val mobileMenuItems: ReactElement = auth match {
      case AuthState.Authenticated(_) =>
        Fragment(
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/") })(
              mui.Icon(muiIcons.Home()),
              texts.home
            ),
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/dashboard") })(
              mui.Icon(muiIcons.Dashboard()),
              texts.dashboard
            ),
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/about") })(
              mui.Icon(muiIcons.Info()),
              texts.about
            ),
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/me") })(
              mui.Icon(muiIcons.Person()),
              texts.profile
            ),
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/signout") })(
              mui.Icon(muiIcons.Logout()),
              texts.signOut
            )
        )
      case AuthState.Unauthenticated =>
        Fragment(
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/") })(
              mui.Icon(muiIcons.Home()),
              texts.home
            ),
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/about") })(
              mui.Icon(muiIcons.Info()),
              texts.about
            ),
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/signup") })(
              mui.Icon(muiIcons.PersonAdd()),
              texts.signUp
            ),
          mui.MenuItem
            .normal()
            .sx(mobileMenuItemSx.asInstanceOf[CSSPropertiesUtils])
            .onClick(_ => { setDrawerOpen(false); history.push("/signin") })(
              mui.Icon(muiIcons.Login()),
              texts.signIn
            )
        )
    }

    val mobileDrawer = mui
      .SwipeableDrawer(
        onOpen = _ => setDrawerOpen(true),
        onClose = _ => setDrawerOpen(false)
      )(
        mui.Box().sx(drawerMenuSx.asInstanceOf[CSSPropertiesUtils])(mobileMenuItems)
      )
      .open(drawerOpen)
      .anchor("right")

    val darkToggle = mui.IconButton
      .normal()(mui.Icon(if (isDark) muiIcons.Brightness7() else muiIcons.Brightness4()))
      .sx(menuIconSx)
      .size("small")
      .onClick(_ => props.ctx.$darkMode := !isDark)

    mui.AppBar
      .sx(appBarSx.asInstanceOf[CSSPropertiesUtils])
      .position("sticky")(
        mui.Container.maxWidth("lg")(
          mui.Toolbar
            .sx(toolbarSx.asInstanceOf[CSSPropertiesUtils])
            .disableGutters(true)(
              brand,
              mui.Box().sx(new CSSPropertiesUtils { flex = 1 }),
              if (isMobile) {
                Fragment(
                  darkToggle,
                  mui.IconButton
                    .normal()(mui.Icon(muiIcons.Menu()))
                    .sx(menuIconSx)
                    .onClick(_ => setDrawerOpen(true)),
                  mobileDrawer
                )
              } else {
                mui
                  .Box()
                  .sx(new CSSPropertiesUtils {
                    display = "flex"; alignItems = "center"; gap = "2px"
                  })(
                    navLinks,
                    mui.Box().sx(js.Dynamic.literal(width = 6).asInstanceOf[CSSPropertiesUtils]),
                    darkToggle,
                    mui.Box().sx(js.Dynamic.literal(width = 8).asInstanceOf[CSSPropertiesUtils]),
                    authBtns
                  )
              }
            )
        )
      )
  }
}

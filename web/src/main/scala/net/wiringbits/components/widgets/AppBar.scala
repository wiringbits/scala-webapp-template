package net.wiringbits.components.widgets

import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
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

  private val appBarSx = js.Dynamic.literal(
    background = "#0E8C6F",
    color = "#FFFFFF",
    borderBottom = "none",
    boxShadow = "0 1px 0 rgba(0,0,0,0.04), 0 8px 24px rgba(0,0,0,0.06)"
  )

  private val toolbarSx = js.Dynamic.literal(
    minHeight = 66,
    display = "flex",
    alignItems = "center"
  )

  private val brandSx = js.Dynamic.literal(
    display = "flex",
    alignItems = "center",
    gap = "10px",
    cursor = "pointer",
    marginRight = 16,
    textDecoration = "none"
  )

  private val logoSx = new CSSPropertiesUtils {
    height = "28px"
    width = "auto"
    display = "block"
  }

  private val brandNameSx = js.Dynamic.literal(
    fontWeight = 800,
    fontSize = 17,
    letterSpacing = "-0.02em",
    color = "#FFFFFF",
    whiteSpace = "nowrap"
  )

  private val navLinkActiveSx = js.Dynamic.literal(
    color = "#FFFFFF",
    fontWeight = 700,
    fontSize = 14.5,
    paddingLeft = 12,
    paddingRight = 12,
    minWidth = 0,
    borderRadius = 6,
    textTransform = "none",
    background = "rgba(255,255,255,0.14)"
  )

  private val navLinkSx = js.Dynamic.literal(
    color = "rgba(255,255,255,0.86)",
    fontWeight = 600,
    fontSize = 14.5,
    paddingLeft = 12,
    paddingRight = 12,
    minWidth = 0,
    borderRadius = 6,
    textTransform = "none"
  )

  private val signInBtnSx = js.Dynamic.literal(
    color = "rgba(255,255,255,0.92)",
    fontWeight = 600,
    textTransform = "none"
  )

  private val signUpBtnSx = js.Dynamic.literal(
    marginLeft = 8,
    backgroundColor = "#FFFFFF",
    color = "#0E8C6F",
    fontWeight = 700,
    textTransform = "none"
  )

  private val signOutBtnSx = js.Dynamic.literal(
    marginLeft = 8,
    color = "#FFFFFF",
    borderColor = "rgba(255,255,255,0.4)",
    textTransform = "none"
  )

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
    val history = useHistory()
    val location = useLocation()
    val pathname = location.asInstanceOf[js.Dynamic].pathname.asInstanceOf[String]
    val isMobile = MediaQueryHooks.useIsMobileOrTablet()
    val (drawerOpen, setDrawerOpen) = Hooks.useState(false)

    val logo = img(
      src := "/img/wiringbits-logo.png",
      alt := "wiringbits",
      style := logoSx
    )

    val brand: ReactElement = mui
      .Box()
      .sx(brandSx.asInstanceOf[CSSPropertiesUtils])
      .onClick(_ => history.push("/"))(
        logo,
        mui
          .Typography("Wiringbits ")
          .sx(brandNameSx.asInstanceOf[CSSPropertiesUtils])
          .component("span")(
            span(style := new CSSPropertiesUtils {
              color = "rgba(255,255,255,0.72)"
              fontWeight = 600
            })("Web App Template")
          )
      )

    def navBtn(label: String, path: String, active: Boolean): ReactElement =
      mui.Button
        .normal()(label)
        .sx((if (active) navLinkActiveSx else navLinkSx).asInstanceOf[CSSPropertiesUtils])
        .onClick(_ => history.push(path))

    val desktopNav: ReactElement = auth match {
      case AuthState.Authenticated(_) =>
        Fragment(
          navBtn(texts.home, "/", pathname == "/"),
          navBtn(texts.dashboard, "/dashboard", pathname == "/dashboard"),
          navBtn(texts.about, "/about", pathname == "/about"),
          navBtn(texts.profile, "/me", pathname == "/me"),
          mui.Button
            .normal()(texts.signOut)
            .sx(signOutBtnSx.asInstanceOf[CSSPropertiesUtils])
            .variant("outlined")
            .size("small")
            .onClick(_ => history.push("/signout"))
        )
      case AuthState.Unauthenticated =>
        Fragment(
          navBtn(texts.home, "/", pathname == "/"),
          navBtn(texts.about, "/about", pathname == "/about"),
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

    mui.AppBar
      .sx(appBarSx.asInstanceOf[CSSPropertiesUtils])
      .position("sticky")(
        mui.Toolbar.sx(toolbarSx.asInstanceOf[CSSPropertiesUtils])(
          brand,
          mui.Box().sx(new CSSPropertiesUtils { flex = 1 }),
          if (isMobile) {
            Fragment(
              mui.IconButton
                .normal()(mui.Icon(muiIcons.Menu()))
                .sx(menuIconSx)
                .onClick(_ => setDrawerOpen(true)),
              mobileDrawer
            )
          } else {
            mui
              .Box()
              .sx(new CSSPropertiesUtils { display = "flex"; alignItems = "center"; gap = "2px" })(
                desktopNav
              )
          }
        )
      )
  }
}

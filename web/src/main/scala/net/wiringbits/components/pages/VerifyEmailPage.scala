package net.wiringbits.components.pages

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, TextAlignProperty}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
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
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.URLSearchParams
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html.{br, className, div}
import typings.reactRouterDom.mod.useLocation
import typings.reactRouterDom.{mod => reactRouterDom}

@react object VerifyEmailPage {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = _ =>
      StringDictionary(
        "emailPage" -> CSSProperties()
          .setFlex(1)
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setAlignItems("center")
          .setTextAlign(TextAlignProperty.center)
          .setJustifyContent("center"),
        "emailTitle" -> CSSProperties()
          .setFontWeight(600),
        "emailPhoto" -> CSSProperties()
          .setWidth("10rem")
          .setPadding("15px 0")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = reactRouterDom.useHistory()
    val params = new URLSearchParams(useLocation().search)
    val emailParam = Option(params.get("email")).getOrElse("")
    val classes = useStyles(())

    Fragment(
      div(className := classes("emailPage"))(
        Fragment(
          mui
            .Typography(texts.verifyYourEmailAddress)
            .variant(muiStrings.h5)
            .className(classes("emailTitle")),
          br(),
          mui
            .Typography(
              texts.emailHasBeenSent
            )
            .variant(muiStrings.h6),
          mui
            .Typography(
              texts.emailNotReceived
            )
            .variant(muiStrings.h6),
          br(),
          mui
            .Button(texts.resendEmail)
            .variant(muiStrings.contained)
            .color(Color.primary)
            .onClick(_ => history.push(s"/resend-verify-email?email=${emailParam}"))
        )
      )
    )
  }

}

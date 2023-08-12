package net.wiringbits.components.pages

import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, TextAlignProperty}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components as mui, materialUiCoreStrings as muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.URLSearchParams
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.Fragment
import slinky.web.html.{br, className, div}
import typings.reactRouterDom.mod.useLocation
import typings.reactRouterDom.mod as reactRouterDom
import org.scalajs.dom

import scala.scalajs.js

object VerifyEmailPage {
  def apply(ctx: AppContext): KeyAddingStage = 
    component(Props(ctx = ctx))
  
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
    val history = reactRouterDom.useHistory().asInstanceOf[js.Dynamic]
    val params = new URLSearchParams(useLocation().asInstanceOf[js.Dynamic].search.asInstanceOf[String])
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

package net.wiringbits.components.pages

import com.olvind.mui.csstype.mod.Property.{FlexDirection, TextAlign}
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.propTypes
import com.olvind.mui.react.mod.CSSProperties
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.{useHistory, useLocation}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom
import org.scalajs.dom.URLSearchParams
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{br, className, div, style}

import scala.scalajs.js

object VerifyEmailPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val emailPageStyling: CSSProperties = new CSSProperties {
    flex = 1
    display = "flex"
    flexDirection = FlexDirection.column
    alignItems = "center"
    textAlign = TextAlign.center
    justifyContent = "center"
  }

  val emailTitleStyling: CSSProperties = new CSSProperties {
    fontWeight = 600
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()
    val params = new URLSearchParams(useLocation().asInstanceOf[js.Dynamic].search.asInstanceOf[String])
    val emailParam = Option(params.get("email")).getOrElse("")

    Fragment(
      div(className := "emailPage", style := emailPageStyling)(
        Fragment(
          mui
            .Typography(texts.verifyYourEmailAddress)
            .variant("h5")
            .className("emailTitle")
            .style(emailTitleStyling),
          br(),
          mui
            .Typography(
              texts.emailHasBeenSent
            )
            .variant("h6"),
          mui
            .Typography(
              texts.emailNotReceived
            )
            .variant("h6"),
          br(),
          mui.Button
            .normal()(texts.resendEmail)
            .variant("contained")
            .color("primary")
            .onClick(_ => history.push(s"/resend-verify-email?email=$emailParam"))
        )
      )
    )
  }

}

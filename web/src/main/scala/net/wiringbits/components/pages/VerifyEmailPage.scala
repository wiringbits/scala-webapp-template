package net.wiringbits.components.pages

import com.olvind.mui.csstype.mod.Property.{FlexDirection, TextAlign}
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.{useHistory, useLocation}
import org.scalajs.dom
import org.scalajs.dom.URLSearchParams
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.br

import scala.scalajs.js

object VerifyEmailPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  private val emailPageStyling = new CSSPropertiesUtils {
    flex = 1
    display = "flex"
    flexDirection = FlexDirection.column
    alignItems = "center"
    textAlign = TextAlign.center
    justifyContent = "center"
  }

  private val emailTitleStyling = new CSSPropertiesUtils {
    fontWeight = 600
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()
    val params = new URLSearchParams(useLocation().asInstanceOf[js.Dynamic].search.asInstanceOf[String])
    val emailParam = Option(params.get("email")).getOrElse("")

    Fragment(
      mui.Box.sx(emailPageStyling)(
        mui
          .Typography(texts.verifyYourEmailAddress)
          .variant("h5")
          .className("emailTitle")
          .sx(emailTitleStyling),
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
  }

}

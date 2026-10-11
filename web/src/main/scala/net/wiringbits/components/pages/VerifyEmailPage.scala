package net.wiringbits.components.pages

import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.{useHistory, useLocation}
import org.scalajs.dom
import org.scalajs.dom.URLSearchParams
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object VerifyEmailPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()
    val params = new URLSearchParams(useLocation().asInstanceOf[js.Dynamic].search.asInstanceOf[String])
    val emailParam = Option(params.get("email")).getOrElse("")

    mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            flex = 1,
            display = "flex",
            alignItems = "center",
            justifyContent = "center",
            padding = "48px 16px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(js.Dynamic.literal(textAlign = "center", maxWidth = 480).asInstanceOf[CSSPropertiesUtils])(
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(
                    width = 84,
                    height = 84,
                    borderRadius = "50%",
                    margin = "0 auto 24px",
                    display = "flex",
                    alignItems = "center",
                    justifyContent = "center",
                    bgcolor = "rgba(14,140,111,0.10)",
                    color = "primary.main"
                  )
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                mui.Icon(muiIcons.MailOutlined()).sx(js.Dynamic.literal(fontSize = 40).asInstanceOf[CSSPropertiesUtils])
              ),
            mui
              .Typography(texts.verifyYourEmailAddress)
              .variant("h4")
              .sx(
                js.Dynamic.literal(marginBottom = "12px", fontWeight = 600).asInstanceOf[CSSPropertiesUtils]
              ),
            mui
              .Typography(
                s"${texts.emailHasBeenSent} ${texts.emailNotReceived}"
              )
              .sx(
                js.Dynamic
                  .literal(color = "text.secondary", fontSize = 16, lineHeight = 1.7, marginBottom = "32px")
                  .asInstanceOf[CSSPropertiesUtils]
              ),
            mui.Button
              .normal()(texts.resendEmail)
              .variant("contained")
              .size("large")
              .startIcon(mui.Icon(muiIcons.Refresh()))
              .onClick(_ => history.push(s"/resend-verify-email?email=$emailParam"))
          )
      )
  }
}

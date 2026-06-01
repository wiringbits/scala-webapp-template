package net.wiringbits.components.pages

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.components.widgets.Logs
import net.wiringbits.core.I18nHooks
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object DashboardPage {
  def apply(ctx: AppContext, user: User): KeyAddingStage =
    component(Props(ctx = ctx, user = user))

  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    Fragment(
      mui
        .Box()
        .sx(js.Dynamic.literal(marginBottom = "32px").asInstanceOf[CSSPropertiesUtils])(
          mui
            .Typography(texts.dashboardPage)
            .variant("h3")
            .sx(
              js.Dynamic
                .literal(fontSize = 34, fontWeight = 700, letterSpacing = "-0.02em")
                .asInstanceOf[CSSPropertiesUtils]
            ),
          mui
            .Typography(texts.welcome(props.user.name))
            .sx(
              js.Dynamic.literal(color = "#5A6A65", fontSize = 16, marginTop = "6px").asInstanceOf[CSSPropertiesUtils]
            )
        ),
      Logs(props.ctx, props.user)
    )
  }
}

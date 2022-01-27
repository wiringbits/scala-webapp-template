package net.wiringbits.components.pages

import net.wiringbits.AppContext
import net.wiringbits.components.widgets.Logs
import net.wiringbits.core.I18nHooks
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle, Title}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object DashboardPage {
  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    Fragment(
      Container(
        margin = Container.EdgeInsets.bottom(16),
        child = Fragment(
          Title(texts.dashboardPage),
          Subtitle(texts.welcome(props.user.name))
        )
      ),
      Logs(props.ctx, props.user)
    )
  }
}

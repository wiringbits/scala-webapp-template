package net.wiringbits.components.pages

import net.wiringbits.components.widgets._
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle, Title}
import net.wiringbits.{AppContext, AppStrings}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object DashboardPage {
  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Fragment(
      Container(
        margin = Container.EdgeInsets.bottom(16),
        child = Fragment(
          Title("Dashboard Page"),
          Subtitle(s"${AppStrings.welcome} ${props.user.name}")
        )
      ),
      Logs(props.ctx.api, props.user)
    )
  }
}

package net.wiringbits.components.pages

import net.wiringbits.components.widgets._
import net.wiringbits.models.User
import net.wiringbits.ui.components.core.widgets._
import net.wiringbits.{API, AppStrings}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object DashboardPage {
  case class Props(api: API, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Fragment(
      Container(
        margin = Container.EdgeInsets.bottom(16),
        child = Fragment(
          Title("Dashboard Page"),
          Subtitle(s"${AppStrings.welcome} ${props.user.name}")
        )
      ),
      Logs(props.api, props.user)
    )
  }
}

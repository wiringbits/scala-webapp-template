package net.wiringbits.components.pages

import net.wiringbits.API
import net.wiringbits.components.widgets.Users
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Subtitle, Title}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object UsersPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Fragment(
      Title("Users Page"),
      Subtitle("User list"),
      Users(props.api)
    )
  }
}

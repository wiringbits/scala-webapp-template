package net.wiringbits.components.pages

import net.wiringbits.API
import net.wiringbits.components.widgets._
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.Alignment
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object SignInPage {
  case class Props(api: API, loggedIn: User => Unit, captchaKey: String)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Container(
      flex = Some(1),
      alignItems = Alignment.center,
      justifyContent = Alignment.center,
      child = SignInForm(props.api, props.loggedIn, props.captchaKey)
    )
  }
}

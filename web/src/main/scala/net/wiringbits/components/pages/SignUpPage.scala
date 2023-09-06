package net.wiringbits.components.pages

import net.wiringbits.AppContext
import net.wiringbits.components.widgets.SignUpForm
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import slinky.core.{FunctionalComponent, KeyAddingStage}

object SignUpPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      justifyContent = Container.Alignment.center,
      child = SignUpForm(props.ctx)
    )
  }
}

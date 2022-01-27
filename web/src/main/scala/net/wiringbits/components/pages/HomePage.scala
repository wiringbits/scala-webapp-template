package net.wiringbits.components.pages

import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets._
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

@react object HomePage {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    div()(
      Title(texts.homePage),
      Subtitle(texts.landingPageContent)
    )
  }
}

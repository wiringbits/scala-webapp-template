package net.wiringbits.components.widgets

import net.wiringbits.AppContext
import net.wiringbits.common.models.Captcha
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import slinky.core.FunctionalComponent
import slinky.core.facade.Hooks
import typings.reactGoogleRecaptcha.components.ReactGoogleRecaptcha

object ReCaptcha {
  case class Props(ctx: AppContext, onChange: Option[Captcha] => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    implicit val ec = props.ctx.executionContext

    // Without useMemo, the component gets rendered everytime the captcha is solved
    Hooks.useMemo(
      () =>
        AsyncComponent.component[String](
          AsyncComponent.Props(
            fetch = () => props.ctx.api.client.getEnvironmentConfig.map(_.recaptchaSiteKey),
            render = recaptchaSiteKey =>
              ReactGoogleRecaptcha(recaptchaSiteKey)
                .onChange(x => props.onChange(Captcha.validate(x.asInstanceOf[String]).toOption))
          )
        ),
      ""
    )
  }
}

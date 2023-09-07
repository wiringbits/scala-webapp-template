package net.wiringbits.components.widgets

import net.wiringbits.AppContext
import net.wiringbits.common.models.Captcha
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import slinky.core.facade.Hooks
import slinky.core.{FunctionalComponent, KeyAddingStage}
import typings.reactGoogleRecaptcha.components.ReactGoogleRecaptcha

import scala.concurrent.ExecutionContext

object ReCaptcha {
  def apply(ctx: AppContext, onChange: Option[Captcha] => Unit): KeyAddingStage =
    component(Props(ctx = ctx, onChange = onChange))

  case class Props(ctx: AppContext, onChange: Option[Captcha] => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    implicit val ec: ExecutionContext = props.ctx.executionContext

    // Without useMemo, the component gets rendered everytime the captcha is solved
    Hooks.useMemo(
      () =>
        AsyncComponent[String](
          fetch = () => props.ctx.api.client.getEnvironmentConfig.map(_.recaptchaSiteKey),
          render = recaptchaSiteKey =>
            ReactGoogleRecaptcha(recaptchaSiteKey)
              .onChange(x => props.onChange(Captcha.validate(x.asInstanceOf[String]).toOption))
        ),
      ""
    )
  }
}

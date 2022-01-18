package net.wiringbits.components.widgets

import net.wiringbits.common.models.Captcha
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import typings.reactGoogleRecaptcha.components.ReactGoogleRecaptcha

@react object ReCaptcha {
  case class Props(onChange: Option[Captcha] => Unit, captchaKey: String)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ReactGoogleRecaptcha(props.captchaKey)
      .onChange(x => props.onChange(Captcha.validate(x.asInstanceOf[String]).toOption))
  }
}

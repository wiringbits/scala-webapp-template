package net.wiringbits.components.widgets

import net.wiringbits.common.models.Captcha
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import typings.reactGoogleRecaptcha.components.ReactGoogleRecaptcha

@react object ReCaptcha {
  case class Props(onChange: Option[Captcha] => Unit)

  private val siteKey = net.wiringbits.BuildInfo.recaptchaKey.filter(_.nonEmpty).getOrElse {
    "6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ReactGoogleRecaptcha(siteKey)
      .onChange(x => props.onChange(Captcha.validate(x.asInstanceOf[String]).toOption))
  }
}

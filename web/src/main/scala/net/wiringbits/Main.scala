package net.wiringbits

import monix.reactive.subjects.Var
import net.wiringbits.core.I18nLang
import net.wiringbits.models.AuthState
import net.wiringbits.webapp.utils.slinkyUtils.components.core.{ErrorBoundaryComponent, ErrorBoundaryInfo}
import org.scalajs.dom
import slinky.hot
import slinky.web.ReactDOM

import scala.scalajs.js.annotation.JSImport
import scala.scalajs.{LinkingInfo, js}

@JSImport("js/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object Main {
  val css = IndexCSS

  def main(argv: Array[String]): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val captchaKey = net.wiringbits.BuildInfo.recaptchaKey.filter(_.nonEmpty).getOrElse {
      "6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI"
    }

    val scheduler = monix.execution.Scheduler.global
    val $authState = Var[AuthState](AuthState.Unauthenticated)(scheduler)
    val $lang = Var[I18nLang](I18nLang.English)(scheduler)
    val ctx = AppContext(API(), recaptchaKey = captchaKey, $authState, $lang)
    val app = ErrorBoundaryComponent(
      ErrorBoundaryComponent.Props(
        child = App(ctx),
        renderError = e => ErrorBoundaryInfo(e)
      )
    )

    ReactDOM.render(app, dom.document.getElementById("root"))
  }
}

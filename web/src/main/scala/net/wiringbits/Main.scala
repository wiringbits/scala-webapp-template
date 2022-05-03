package net.wiringbits

import monix.reactive.subjects.Var
import net.wiringbits.common.models.Email
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

    val scheduler = monix.execution.Scheduler.global
    val $authState = Var[AuthState](AuthState.Unauthenticated)(scheduler)
    val $lang = Var[I18nLang](I18nLang.English)(scheduler)
    val ctx = AppContext(
      API(),
      $authState,
      $lang,
      Email.trusted("hello@wiringbits.net"),
      "+52 (999) 9999 999",
      org.scalajs.macrotaskexecutor.MacrotaskExecutor
    )
    val app = ErrorBoundaryComponent(
      ErrorBoundaryComponent.Props(
        child = App(ctx),
        renderError = e => ErrorBoundaryInfo(e)
      )
    )

    ReactDOM.render(app, dom.document.getElementById("root"))
  }
}

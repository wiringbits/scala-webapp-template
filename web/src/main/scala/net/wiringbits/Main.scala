package net.wiringbits

import net.wiringbits.ui.components.core.{ErrorBoundaryComponent, ErrorBoundaryInfo}
import org.scalajs.dom
import slinky.hot
import slinky.web.ReactDOM

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.{LinkingInfo, js}
import scala.scalajs.js.annotation.JSImport

@JSImport("js/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object Main {
  val css = IndexCSS

  def main(argv: Array[String]): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val app = ErrorBoundaryComponent(
      ErrorBoundaryComponent.Props(
        child = App(API()),
        renderError = e => ErrorBoundaryInfo(e)
      )
    )

    ReactDOM.render(app, dom.document.getElementById("root"))
  }
}

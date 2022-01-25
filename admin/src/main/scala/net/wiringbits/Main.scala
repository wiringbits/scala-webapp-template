package net.wiringbits

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

    val app = ErrorBoundaryComponent(
      ErrorBoundaryComponent.Props(
        child = App(API()),
        renderError = e => ErrorBoundaryInfo(e)
      )
    )

    ReactDOM.render(app, dom.document.getElementById("root"))
  }
}

package net.wiringbits

import net.wiringbits.webapp.utils.ui.web.AdminView
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

import scala.util.{Failure, Success}

object Main {

  private def App = {
    val api = API()
    AdminView.component(api.admin)
  }

  def main(argv: Array[String]): Unit = {
    App.onComplete {
      case Success(app) => app.render.renderIntoDOM(dom.document.getElementById("root"))
      case Failure(ex) => ex.printStackTrace()
    }
  }
}

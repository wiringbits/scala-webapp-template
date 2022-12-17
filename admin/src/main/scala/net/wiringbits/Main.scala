package net.wiringbits

import io.github.nafg.simplefacade.Factory
import net.wiringbits.webapp.utils.ui.webTest.AdminView
import net.wiringbits.webapp.utils.ui.webTest.facades.reactadmin.Admin
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main {

  private def App: Future[Factory[Admin.Props]] = {
    val api = API()
    AdminView(api.admin)
  }

  def main(argv: Array[String]): Unit = {
    App.onComplete {
      case Success(app) => app.render.renderIntoDOM(dom.document.getElementById("root"))
      case Failure(ex) => ex.printStackTrace()
    }
  }
}

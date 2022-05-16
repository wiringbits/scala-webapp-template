package net.wiringbits

import japgolly.scalajs.react.vdom.VdomNode
import org.scalajs.dom
import facades.reactadmin._
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

import scala.util.{Failure, Success}

object Main {

  private def AdminTables(response: AdminGetTables.Response) = {
    val tablesUrl = s"${API.apiUrl}/admin/tables"
    val tableNames = response.data.map(_.name)

    def buildResources: List[VdomNode] = {
      tableNames.map { tableName =>
        Resource(
          _.name := tableName,
          _.list := ReactAdmin.ListGuesser,
          _.edit := ReactAdmin.EditGuesser
        )
      }
    }

    val resources = buildResources
    Admin(
      _.dataProvider :=
        simpleRestProvider(tablesUrl)
    )(resources: _*)
  }

  private def AdminView(api: API) = {
    api.admin.client.getTables.map { response =>
      AdminTables(response)
    }
  }

  private def App = {
    AdminView(API())
  }

  def main(argv: Array[String]): Unit = {
    App.onComplete {
      case Success(app) => app.render.renderIntoDOM(dom.document.getElementById("root"))
      case Failure(ex) => ex.printStackTrace()
    }
  }
}

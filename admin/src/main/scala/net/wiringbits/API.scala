package net.wiringbits

import net.wiringbits.api.ApiClient
import net.wiringbits.webapp.utils.api.AdminDataExplorerApiClient
import net.wiringbits.webapp.utils.ui.web.{API => APIAdmin}

import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

case class API(client: ApiClient, admin: APIAdmin)

object API {

  // allows overriding the server url
  private val apiUrl = {
    net.wiringbits.BuildInfo.apiUrl.filter(_.nonEmpty).getOrElse {
      "http://localhost:9000"
    }
  }

  def apply(): API = {
    println(s"Server API expected at: $apiUrl")

    implicit val sttpBackend = sttp.client3.FetchBackend()
    val client = new ApiClient.DefaultImpl(ApiClient.Config(apiUrl))
    val admin = new AdminDataExplorerApiClient.DefaultImpl(AdminDataExplorerApiClient.Config(apiUrl))
    val adminApi = APIAdmin(admin, apiUrl)
    API(client, adminApi)
  }
}

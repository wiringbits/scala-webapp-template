package net.wiringbits

import net.wiringbits.api.ApiClient
import net.wiringbits.webapp.utils.api.AdminDataExplorerApiClient
import net.wiringbits.webapp.utils.ui.web.{API => APIAdmin}
import scala.concurrent.ExecutionContext

case class APIs(client: API, admin: APIAdmin)

case class API(client: ApiClient)

object APIs {

  // allows overriding the server url
  private val apiUrl = {
    net.wiringbits.BuildInfo.apiUrl.filter(_.nonEmpty).getOrElse {
      "http://localhost:9000"
    }
  }

  def apply()(implicit ec: ExecutionContext): APIs = {
    println(s"Server API expected at: $apiUrl")

    implicit val sttpBackend = sttp.client.FetchBackend()
    val client = new ApiClient.DefaultImpl(ApiClient.Config(apiUrl))
    val admin = new AdminDataExplorerApiClient.DefaultImpl(AdminDataExplorerApiClient.Config(apiUrl))
    val clientApi = API(client)
    val adminApi = APIAdmin(admin)
    APIs(client = clientApi, admin = adminApi)
  }
}

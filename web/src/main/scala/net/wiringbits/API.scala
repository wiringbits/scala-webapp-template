package net.wiringbits

import net.wiringbits.api.ApiClient
import net.wiringbits.services.StorageService

import scala.concurrent.ExecutionContext

case class API(client: ApiClient, storage: StorageService)

object API {

  // allows overriding the server url
  private val apiUrl = {
    net.wiringbits.BuildInfo.apiUrl.filter(_.nonEmpty).getOrElse {
      "http://localhost:9000"
    }
  }

  def apply()(implicit ec: ExecutionContext): API = {
    println(s"Server API expected at: $apiUrl")

    implicit val sttpBackend = sttp.client.FetchBackend()
    val client = new ApiClient.DefaultImpl(ApiClient.Config(apiUrl))
    val storage = new StorageService

    API(client, storage)
  }
}

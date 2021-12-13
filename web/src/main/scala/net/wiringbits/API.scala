package net.wiringbits

import net.wiringbits.api.ApiClient
import net.wiringbits.services.StorageService

import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

case class API(client: ApiClient, storage: StorageService)

object API {

  // allows overriding the server url
  private val apiUrl = {
    net.wiringbits.BuildInfo.apiUrl.filter(_.nonEmpty).getOrElse {
      "http://localhost:9000"
    }
  }

  def apply(): API = {
    println(s"Server API expected at: $apiUrl")

    implicit val sttpBackend = sttp.client.FetchBackend()
    val client = new ApiClient.DefaultImpl(ApiClient.Config(apiUrl))
    val storage = new StorageService

    API(client, storage)
  }
}

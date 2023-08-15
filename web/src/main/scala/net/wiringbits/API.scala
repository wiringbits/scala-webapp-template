package net.wiringbits

import net.wiringbits.api.{ApiClient, Config, SttpTapirApiClient}
import net.wiringbits.services.StorageService
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.*
import sttp.client3.SttpBackend

import scala.concurrent.Future

case class API(client: ApiClient, storage: StorageService, tapirClient: SttpTapirApiClient)

object API {

  // allows overriding the server url
  private val apiUrl = {
    net.wiringbits.BuildInfo.apiUrl.filter(_.nonEmpty).getOrElse {
      "http://localhost:8080/api"
    }
  }

  def apply(): API = {
    println(s"Server API expected at: $apiUrl")

    implicit val sttpBackend: SttpBackend[Future, _] = sttp.client3.FetchBackend()
    val tapirClient = new SttpTapirApiClient(Config(apiUrl))
    val client = new ApiClient.DefaultImpl(ApiClient.Config(apiUrl))
    val storage = new StorageService

    API(client, storage, tapirClient)
  }
}

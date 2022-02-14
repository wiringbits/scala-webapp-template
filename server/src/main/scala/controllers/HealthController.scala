package controllers

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.stream.Materializer
import controllers.codecs.PeerCodecs
import net.wiringbits.actors.PeerActor
import net.wiringbits.modules.TopicActor
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HealthController @Inject() (
    cc: ControllerComponents,
    actor: ActorRef[String],
    topic: TopicActor
)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
    extends AbstractController(cc) {
  import HealthController._
  import akka.actor.typed.pubsub.Topic

  def check() = Action { _ =>
    actor ! "Sample msg"
    topic.ref ! Topic.Subscribe(actor)
    topic.ref ! Topic.Publish("Hello Subscribers!")
    Ok("")
  }

  def getWs(): WebSocket = WebSocket.acceptOrResult[JsValue, PeerActor.Event] { _ =>
    def flow = ActorFlow.actorRef { client =>
      PeerActor.props(client)
    }

    Future.successful(Right(flow))
  }
}

object HealthController extends PeerCodecs {

  implicit val transformer: WebSocket.MessageFlowTransformer[JsValue, PeerActor.Event] = {
    WebSocket.MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, PeerActor.Event]
  }
}

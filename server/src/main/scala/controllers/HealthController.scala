package controllers

import akka.actor.ActorSystem
import akka.actor.typed.pubsub.Topic
import akka.stream.Materializer
import controllers.codecs.PeerCodecs
import net.wiringbits.actors.{ClusterSubscriberActor, ConnectionManagerActor, PeerActor}
import net.wiringbits.models.{UserDescriptor, UserPointsChangedEvent}
import net.wiringbits.modules.TopicActor
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HealthController @Inject() (
    cc: ControllerComponents,
    clusterSubscriber: ClusterSubscriberActor,
    topic: TopicActor,
    connectionManager: ConnectionManagerActor.Ref
)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
    extends AbstractController(cc) {
  import HealthController._

  def check() = Action { _ =>
    topic.ref ! Topic.Subscribe(clusterSubscriber.ref)
    topic.ref ! Topic.Publish(UserPointsChangedEvent(1, 500))
    Ok("")
  }

  def getWs(): WebSocket = WebSocket.acceptOrResult[JsValue, PeerActor.Event] { _ =>
    def flow = ActorFlow.actorRef { client =>
      val userDescriptor = UserDescriptor(1)
      PeerActor.props(client, connectionManager, userDescriptor)
    }

    Future.successful(Right(flow))
  }
}

object HealthController extends PeerCodecs {

  implicit val transformer: WebSocket.MessageFlowTransformer[JsValue, PeerActor.Event] = {
    WebSocket.MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, PeerActor.Event]
  }
}

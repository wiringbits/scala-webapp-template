package controllers

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.pubsub.DistributedPubSub
import net.wiringbits.modules.TopicActor
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject

class HealthController @Inject() (
    cc: ControllerComponents,
    actor: ActorRef[String],
    topic: TopicActor
) extends AbstractController(cc) {
  import akka.actor.typed.pubsub.Topic

  def check() = Action { _ =>
    actor ! "Sample msg"
    topic.ref ! Topic.Subscribe(actor)
    topic.ref ! Topic.Publish("Hello Subscribers!")
    Ok("")
  }
}

package net.wiringbits.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.pubsub.Topic
import net.wiringbits.actors.TopicEventsActor.Message

case class TopicEventsActor(ref: ActorRef[Topic.Command[Message]])

object TopicEventsActor {
  trait Message
  case class UserPointsEvent(userId: Int, points: Int) extends Message
}

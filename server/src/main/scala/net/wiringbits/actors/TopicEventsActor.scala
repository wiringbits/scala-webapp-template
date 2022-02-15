package net.wiringbits.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.pubsub.Topic
import net.wiringbits.models.UserMessage

case class TopicEventsActor(ref: ActorRef[Topic.Command[UserMessage]])

package net.wiringbits.actors

import akka.actor.typed.ActorRef
import net.wiringbits.actors.TopicEventsActor.Message

case class EventManagerActor(ref: ActorRef[Message])

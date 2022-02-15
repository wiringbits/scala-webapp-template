package net.wiringbits.actors

import akka.actor.typed.ActorRef
import net.wiringbits.models.UserMessage

case class ClusterSubscriberActor(ref: ActorRef[UserMessage]) {}

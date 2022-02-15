package net.wiringbits.actors

import akka.actor.ActorRef
import net.wiringbits.models.UserDescriptor

case class PeerUser(
    userDescriptor: UserDescriptor,
    clientRef: ActorRef
)

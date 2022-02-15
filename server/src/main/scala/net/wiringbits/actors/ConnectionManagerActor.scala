package net.wiringbits.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import net.wiringbits.actors.ConnectionManagerActor.Command.{Connect, Disconnect, SendMessage}
import net.wiringbits.models.{ConnectionManagerState, UserMessage, UserPointsChangedEvent}

import javax.inject.Inject

class ConnectionManagerActor @Inject() extends Actor with ActorLogging {
  override def receive: Receive = { case message =>
    log.info(s"Unexpected message: $message")
  }

  override def preStart(): Unit = {
    log.info("ConnectionManagerActor starting")
    context.become(withState(ConnectionManagerState.empty))
  }

  private def withState(state: ConnectionManagerState): Receive = {

    case Connect(client) =>
      context.become(withState(state + client))

    case Disconnect(client) =>
      context.become(withState(state - client))

    case SendMessage(message) =>
      message match {
        case UserPointsChangedEvent(userId, points) =>
          state.clients.find(_.userDescriptor.userId == userId).foreach { x =>
            x.clientRef ! PeerActor.Event.UserPointsChanged(points)
          }
      }

    case message =>
      log.info(s"Unexpected message: $message")
  }
}

object ConnectionManagerActor {

  def props(
  ): Props = {
    Props(new ConnectionManagerActor())
  }

  final class Ref private (val ref: ActorRef)

  object Ref {

    def apply(name: String = "connection-manager")(implicit system: ActorSystem): Ref = {
      val actor = system.actorOf(props(), name)
      new Ref(actor)
    }
  }

  sealed trait Command extends Product with Serializable

  object Command {
    final case class Connect(peerUser: PeerUser) extends Command
    final case class Disconnect(peerUser: PeerUser) extends Command
    final case class SendMessage(message: UserMessage) extends Command
  }
}

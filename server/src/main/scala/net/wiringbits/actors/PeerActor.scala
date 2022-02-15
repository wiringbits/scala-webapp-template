package net.wiringbits.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import net.wiringbits.actors.PeerActor.{Command, Event}
import net.wiringbits.models.UserDescriptor
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}

class PeerActor(client: ActorRef, connectionManager: ConnectionManagerActor.Ref, userDescriptor: UserDescriptor)(
    implicit reads: Reads[PeerActor.Command]
) extends Actor
    with ActorLogging {

  private val peerUser = PeerUser(userDescriptor, client)

  override def preStart(): Unit = {
    log.info(s"Peer connected: $self")
    connectionManager.ref ! ConnectionManagerActor.Command.Connect(peerUser)
    context become handleMessagesReceived
  }

  override def postStop(): Unit = {
    onClientDisconnected()
  }

  private def onClientDisconnected(): Unit = {
    connectionManager.ref ! ConnectionManagerActor.Command.Disconnect(peerUser)
  }

  override def receive: Receive = { case x =>
    log.info(s"Unexpected message $x")
  }

  private def handleMessagesReceived: Receive = {

    /** The Command decoding is done here to be able to report validation issues.
      *
      * It seems that play has no way to do that.
      */
    case json: JsValue =>
      json.validate[Command] match {
        case JsSuccess(command, _) =>
          self ! command

        case JsError(errors) =>
          val msj = s"Failed to decode command: ${errors.mkString(", ")}"
          log.debug(msj)
          client ! PeerActor.Event.UnknownCommand(msj)
      }

    case Command.Ping(_) =>
      log.info("PING RECEIVED")
      client ! PeerActor.Event.Pong()

    case Event.UserPointsChanged(points) =>
      client ! Event.UserPointsChanged(points)
    case x =>
      val msj = s"withState - Unexpected message: $x"
      log.warning(msj)
      client ! PeerActor.Event.UnknownCommand(msj)
  }
}

object PeerActor {

  def props(client: ActorRef, connectionManager: ConnectionManagerActor.Ref, userDescriptor: UserDescriptor)(implicit
      reads: Reads[Command]
  ): Props = {

    Props(new PeerActor(client, connectionManager, userDescriptor))
  }

  sealed trait Command extends Product with Serializable
  object Command {
    final case class Ping(noData: String = "") extends Command
  }

  sealed trait Event extends Product with Serializable
  object Event {
    final case class Pong(noData: String = "") extends Event
    final case class UnknownCommand(error: String) extends Event
    final case class UserPointsChanged(points: Int) extends Event
  }
}

package net.wiringbits.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import net.wiringbits.actors.PeerActor.Command
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}

class PeerActor(client: ActorRef)(implicit reads: Reads[PeerActor.Command]) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info(s"Peer connected: $self")
    context become handleMessagesReceived
  }

  override def postStop(): Unit = {
    onClientDisconnected()
  }

  private def onClientDisconnected(): Unit = {}

  override def receive: Receive = { case x =>
    println(s"unexpected message $x")
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
          // TODO: Return errors
          log.debug(s"Failed to decode command: ${errors.mkString(", ")}")
      }

    case Command.Ping(_) =>
      client ! PeerActor.Event.Pong

    case x =>
      val msj = s"withState - Unexpected message: $x"
      log.warning(msj)
      client ! PeerActor.Event.UnknownCommand(msj)
  }
}

object PeerActor {

  def props(client: ActorRef)(implicit reads: Reads[Command]): Props = {

    Props(new PeerActor(client))
  }

  sealed trait Command extends Product with Serializable
  object Command {
    final case class Ping(noData: String = "") extends Command
  }

  sealed trait Event extends Product with Serializable
  object Event {
    final case class Pong(noData: String = "") extends Event
    final case class UnknownCommand(error: String) extends Event
  }
}

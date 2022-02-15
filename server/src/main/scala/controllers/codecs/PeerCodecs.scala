package controllers.codecs

import controllers.CommonCodecs
import net.wiringbits.actors.PeerActor.{Command, Event}
import play.api.libs.json.{Json, Reads, Writes}

trait PeerCodecs extends CommonCodecs {

  private val pingReads: Reads[Command.Ping] = Json.reads[Command.Ping]

  implicit val commandReads: Reads[Command] = readsADT { case ("Ping", json) =>
    json.validate[Command.Ping](pingReads)

  }

  private implicit val pongWrites: Writes[Event.Pong] = Json.writes[Event.Pong]
  private implicit val unknownCommandWrites: Writes[Event.UnknownCommand] = Json.writes[Event.UnknownCommand]
  private implicit val userPointUpdated: Writes[Event.UserPointsChanged] = Json.writes[Event.UserPointsChanged]

  implicit val eventWrites: Writes[Event] = writesADT[Event] {
    case obj: Event.Pong =>
      "Pong" -> Json.toJson(obj)(pongWrites)
    case obj: Event.UnknownCommand => "UnknownCommand" -> Json.toJson(obj)(unknownCommandWrites)
    case obj: Event.UserPointsChanged => "UserPointsUpdated" -> Json.toJson(obj)(userPointUpdated)
  }
}

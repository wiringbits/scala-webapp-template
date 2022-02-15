package net.wiringbits.models

import net.wiringbits.actors.PeerUser

case class ConnectionManagerState(clients: Set[PeerUser]) {
  def +(client: PeerUser): ConnectionManagerState = copy(clients = clients + client)

  def -(client: PeerUser): ConnectionManagerState = copy(clients = clients - client)
}

object ConnectionManagerState {
  def empty: ConnectionManagerState = ConnectionManagerState(Set.empty)
}

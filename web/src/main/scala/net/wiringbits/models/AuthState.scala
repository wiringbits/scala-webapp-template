package net.wiringbits.models

sealed trait AuthState extends Product with Serializable

object AuthState {
  final case object Unauthenticated extends AuthState
  final case class Authenticated(user: User) extends AuthState
}

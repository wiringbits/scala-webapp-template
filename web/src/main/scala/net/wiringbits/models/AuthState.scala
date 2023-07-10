package net.wiringbits.models

sealed trait AuthState extends Product with Serializable

object AuthState {
  case object Unauthenticated extends AuthState
  case class Authenticated(user: User) extends AuthState

  implicit val authStateEq: cats.Eq[AuthState] = cats.Eq.fromUniversalEquals
}

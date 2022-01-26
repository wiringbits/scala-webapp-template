package net.wiringbits

import monix.reactive.subjects.Var
import net.wiringbits.models.{AuthState, User}

case class AppContext(api: API, recaptchaKey: String, auth: Var[AuthState]) {
  def loggedIn(user: User): Unit = {
    api.storage.saveJwt(user.jwt)
    auth := AuthState.Authenticated(user)
  }

  def loggedOut(): Unit = {
    api.storage.saveJwt("")
    auth := AuthState.Unauthenticated
  }
}

package net.wiringbits

import monix.reactive.subjects.Var
import net.wiringbits.common.models.Email
import net.wiringbits.core.I18nLang
import net.wiringbits.models.{AuthState, User}

case class AppContext(
    api: API,
    recaptchaKey: String,
    $auth: Var[AuthState],
    $lang: Var[I18nLang],
    contactEmail: Email,
    contactPhone: String
) {

  // TODO: This is hacky but it works while preventing to pollute all components from depending on the Texts
  //       still, it would be ideal to keep a Var with the current Texts instance
  def texts(lang: I18nLang): I18nMessages = new I18nMessages(lang)

  def loggedIn(user: User): Unit = {
    api.storage.saveJwt(user.jwt)
    $auth := AuthState.Authenticated(user)
  }

  def loggedOut(): Unit = {
    api.storage.saveJwt("")
    $auth := AuthState.Unauthenticated
  }

  def switchLang(newLang: I18nLang): Unit = {
    api.storage.saveLang(newLang)
    $lang := newLang
  }
}

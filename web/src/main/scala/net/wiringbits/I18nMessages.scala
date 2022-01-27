package net.wiringbits

import net.wiringbits.core.I18nLang

// TODO: conditionaly render messages when we support more than 1 language
class I18nMessages(_lang: I18nLang) {

  def appName = "Wiringbits Web App Template"
  def profile = "Profile"
  def home = "Home"
  def dashboard = "Dashboard"
  def about = "About"
  def signOut = "Sign out"
  def signUp: String = "Sign up"
  def signIn: String = "Sign in"
  def loading: String = "Loading"
}

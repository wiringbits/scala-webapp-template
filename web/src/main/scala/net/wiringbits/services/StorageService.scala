package net.wiringbits.services

import org.scalajs.dom

class StorageService {

  def saveJwt(jwt: String): Unit = {
    dom.window.localStorage.setItem("jwt", jwt)
  }

  def findJwt: Option[String] = {
    Option(dom.window.localStorage.getItem("jwt"))
      .filter(_.nonEmpty)
  }
}

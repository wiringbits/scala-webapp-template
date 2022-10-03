package net.wiringbits.services

import net.wiringbits.core.I18nLang
import org.scalajs.dom

class StorageService {
  def saveLang(lang: I18nLang): Unit = save("lang", lang.toString)
  def findLang(): Option[I18nLang] = find("lang").flatMap(I18nLang.from)

  private def save(key: String, value: String): Unit = {
    dom.window.localStorage.setItem(key, value)
  }

  private def find(key: String): Option[String] = {
    Option(dom.window.localStorage.getItem(key))
      .filter(_.nonEmpty)
  }
}

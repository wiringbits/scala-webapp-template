package net.wiringbits.core

sealed trait I18nLang extends Product with Serializable

object I18nLang {
  final case object English extends I18nLang

  val values: List[I18nLang] = List(English)

  def from(string: String): Option[I18nLang] = {
    values.find(_.toString.toLowerCase == string.toLowerCase)
  }

  implicit val catsEq: cats.Eq[I18nLang] = cats.Eq.fromUniversalEquals
}

package net.wiringbits.core

import monix.reactive.subjects.Var
import net.wiringbits.I18nMessages
import slinky.core.facade.Hooks

object I18nHooks {
  def useMessages($lang: Var[I18nLang]): I18nMessages = {
    val lang = ReactiveHooks.useDistinctValue($lang)
    Hooks.useMemo(() => new I18nMessages(lang), List(lang))
  }
}

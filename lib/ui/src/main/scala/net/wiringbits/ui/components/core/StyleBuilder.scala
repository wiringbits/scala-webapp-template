package net.wiringbits.ui.components.core

import org.scalablytyped.runtime.StringDictionary
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod._

import scala.scalajs.js

/* This is an example of a scala facade on top of the generated code.
 * Note that you can do all this without casting, but type inference is not perfect.
 */
object StyleBuilder {

  @inline def apply[Theme, Props <: js.Object]: StyleBuilder[Theme, Props] =
    new StyleBuilder[Theme, Props](_ => StringDictionary.empty)
}

@inline final class StyleBuilder[T, P] private (val f: StyleRulesCallback[T, P, String]) extends AnyVal {

  @inline def add(key: String, value: CSSProperties): StyleBuilder[T, P] =
    new StyleBuilder[T, P]({ theme =>
      val ret = f(theme)
      ret.update(key, value)
      ret
    })

  @inline def add(key: String, withTheme: T => CSSProperties): StyleBuilder[T, P] =
    new StyleBuilder[T, P]({ theme =>
      val ret = this.f(theme)
      ret.update(key, withTheme(theme))
      ret
    })

  @inline def add(key: String, withThemeProps: (T, P) => CSSProperties): StyleBuilder[T, P] =
    new StyleBuilder[T, P]({ theme =>
      val ret: StyleRules[P, String] = this.f(theme)
      val x: js.Function1[P, CSSProperties] = (props: P) => withThemeProps(theme, props)
      ret.update(key, x)
      ret
    })

  @inline def hook: StylesHook[Styles[T, P, String]] =
    makeStyles[Styles[T, P, String]](f)

  @inline def hook(opts: WithStylesOptions): StylesHook[Styles[T, P, String]] =
    makeStyles[Styles[T, P, String]](f, opts)
}

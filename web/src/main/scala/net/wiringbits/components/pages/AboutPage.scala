package net.wiringbits.components.pages

import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.*

import scala.scalajs.js

object AboutPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            flex = 1,
            display = "flex",
            alignItems = "center",
            justifyContent = "center",
            padding = "48px 16px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Card()
          .variant("outlined")
          .sx(
            js.Dynamic
              .literal(
                borderRadius = "16px",
                maxWidth = 520,
                width = "100%",
                textAlign = "center",
                borderColor = "#E6ECEA",
                boxShadow = "0 18px 50px rgba(0,0,0,0.06)"
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .CardContent()
              .sx(js.Dynamic.literal(padding = "48px 48px 40px").asInstanceOf[CSSPropertiesUtils])(
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(display = "flex", justifyContent = "center", marginBottom = "20px")
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    img(
                      src := "/img/wiringbits-logo.png",
                      alt := "wiringbits",
                      style := new CSSPropertiesUtils { height = "80px"; width = "auto" }
                    )
                  ),
                mui
                  .Typography("wiringbits")
                  .sx(
                    js.Dynamic
                      .literal(
                        fontFamily = """ui-monospace, SFMono-Regular, "SF Mono", Menlo, Consolas, monospace""",
                        fontWeight = 700,
                        fontSize = 38,
                        letterSpacing = "-0.01em",
                        marginBottom = "12px",
                        display = "block"
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  ),
                mui
                  .Typography(
                    "A reusable, production-ready skeleton for Scala web applications — open source and battle-tested across real products."
                  )
                  .sx(
                    js.Dynamic
                      .literal(
                        color = "#5A6A65",
                        fontSize = 15.5,
                        lineHeight = 1.7,
                        marginBottom = "32px",
                        maxWidth = 380,
                        margin = "0 auto 32px"
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  ),
                a(
                  slinky.web.html.href := "https://github.com/wiringbits/scala-webapp-template",
                  target := "_blank",
                  rel := "noopener noreferrer",
                  style := new CSSPropertiesUtils { textDecoration = "none" }
                )(
                  mui.Button
                    .normal()(texts.checkoutTheRepo)
                    .variant("contained")
                    .size("large")
                    .startIcon(mui.Icon(muiIcons.GitHub()))
                    .endIcon(mui.Icon(muiIcons.ArrowForward()))
                )
              )
          )
      )
  }
}

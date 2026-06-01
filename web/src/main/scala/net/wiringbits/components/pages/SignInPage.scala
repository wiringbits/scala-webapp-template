package net.wiringbits.components.pages

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.components.widgets.SignInForm
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.useHistory
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.*

import scala.scalajs.js

object SignInPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()

    mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            flex = 1,
            display = "flex",
            alignItems = "center",
            justifyContent = "center",
            padding = "48px 16px 64px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(js.Dynamic.literal(width = "100%", maxWidth = 440).asInstanceOf[CSSPropertiesUtils])(
            // auth shell header
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(
                    display = "flex",
                    flexDirection = "column",
                    alignItems = "center",
                    gap = "8px",
                    marginBottom = "24px",
                    color = "primary.main"
                  )
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                img(
                  src := "/img/wiringbits-logo.png",
                  alt := "wiringbits",
                  style := new CSSPropertiesUtils { height = "36px"; width = "auto" }
                ),
                mui
                  .Typography(texts.signIn)
                  .variant("h4")
                  .sx(
                    js.Dynamic.literal(color = "text.primary", marginTop = "8px").asInstanceOf[CSSPropertiesUtils]
                  )
              ),
            // card
            mui
              .Card()
              .variant("outlined")
              .sx(
                js.Dynamic
                  .literal(
                    borderRadius = "12px",
                    borderColor = "divider",
                    boxShadow = "0 1px 2px rgba(0,0,0,0.04), 0 18px 50px rgba(0,0,0,0.07)"
                  )
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                mui
                  .CardContent()
                  .sx(js.Dynamic.literal(padding = "32px").asInstanceOf[CSSPropertiesUtils])(
                    mui
                      .Box()
                      .sx(
                        js.Dynamic
                          .literal(display = "flex", flexDirection = "column", gap = "20px")
                          .asInstanceOf[CSSPropertiesUtils]
                      )(
                        SignInForm(props.ctx),
                        mui
                          .Box()
                          .sx(
                            js.Dynamic
                              .literal(
                                display = "flex",
                                flexDirection = "column",
                                gap = "6px",
                                alignItems = "center",
                                paddingTop = "4px"
                              )
                              .asInstanceOf[CSSPropertiesUtils]
                          )(
                            mui
                              .Typography()
                              .sx(
                                js.Dynamic
                                  .literal(fontSize = 14, color = "text.secondary")
                                  .asInstanceOf[CSSPropertiesUtils]
                              )(
                                s"${texts.dontHaveAccountYet} ",
                                mui
                                  .Link(texts.signUp)
                                  .sx(
                                    js.Dynamic
                                      .literal(fontSize = 14, cursor = "pointer")
                                      .asInstanceOf[CSSPropertiesUtils]
                                  )
                                  .component("button")
                                  .onClick(_ => history.push("/signup"))
                              ),
                            mui
                              .Typography()
                              .sx(
                                js.Dynamic
                                  .literal(fontSize = 14, color = "text.secondary")
                                  .asInstanceOf[CSSPropertiesUtils]
                              )(
                                s"${texts.forgotYourPassword} ",
                                mui
                                  .Link(texts.recoverIt)
                                  .sx(
                                    js.Dynamic
                                      .literal(fontSize = 14, cursor = "pointer")
                                      .asInstanceOf[CSSPropertiesUtils]
                                  )
                                  .component("button")
                                  .onClick(_ => history.push("/forgot-password"))
                              )
                          )
                      )
                  )
              )
          )
      )
  }
}

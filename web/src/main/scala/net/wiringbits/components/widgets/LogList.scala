package net.wiringbits.components.widgets

import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.api.models.users.GetUserLogs
import net.wiringbits.api.utils.Formatter
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object LogList {
  def apply(ctx: AppContext, response: GetUserLogs.Response, forceRefresh: () => Unit): KeyAddingStage =
    component(Props(ctx = ctx, response = response, forceRefresh = forceRefresh))

  case class Props(ctx: AppContext, response: GetUserLogs.Response, forceRefresh: () => Unit)

  private val primary = "#0E8C6F"
  private val divider = "#E6ECEA"

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val items = props.response.data

    mui
      .Card()
      .variant("outlined")
      .sx(
        js.Dynamic.literal(borderRadius = 12, borderColor = divider).asInstanceOf[CSSPropertiesUtils]
      )(
        // card header
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                px = "24px",
                py = "16px",
                display = "flex",
                alignItems = "center",
                borderBottom = s"1px solid $divider"
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .Typography(texts.logs)
              .variant("h6")
              .sx(
                js.Dynamic.literal(fontSize = 17).asInstanceOf[CSSPropertiesUtils]
              ),
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(
                    ml = "12px",
                    px = "8px",
                    py = "2px",
                    borderRadius = 999,
                    bgcolor = "rgba(14,140,111,0.10)",
                    color = primary,
                    fontSize = 12.5,
                    fontWeight = 700
                  )
                  .asInstanceOf[CSSPropertiesUtils]
              )(items.length.toString),
            mui.Box().sx(js.Dynamic.literal(flex = 1).asInstanceOf[CSSPropertiesUtils]),
            mui.Button
              .normal()(texts.reload)
              .size("small")
              .startIcon(mui.Icon(muiIcons.Refresh()))
              .onClick(_ => props.forceRefresh())
          ),
        // log rows
        mui.Box()(
          items.zipWithIndex.map { case (item, i) =>
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(
                    display = "flex",
                    gap = "16px",
                    px = "24px",
                    py = "18px",
                    borderBottom = if (i < items.length - 1) s"1px solid $divider" else "none"
                  )
                  .asInstanceOf[CSSPropertiesUtils]
              )
              .withKey(item.userLogId.toString)(
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(
                        width = 38,
                        height = 38,
                        flex = "0 0 auto",
                        borderRadius = 8,
                        display = "flex",
                        alignItems = "center",
                        justifyContent = "center",
                        bgcolor = "rgba(14,140,111,0.10)",
                        color = primary
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    mui.Icon(muiIcons.History()).sx(js.Dynamic.literal(fontSize = 20).asInstanceOf[CSSPropertiesUtils])
                  ),
                mui
                  .Box()
                  .sx(js.Dynamic.literal(minWidth = 0).asInstanceOf[CSSPropertiesUtils])(
                    mui
                      .Typography(item.message)
                      .sx(
                        js.Dynamic
                          .literal(fontWeight = 600, fontSize = 14.5, color = "#15201D")
                          .asInstanceOf[CSSPropertiesUtils]
                      ),
                    mui
                      .Typography(Formatter.instant(item.createdAt))
                      .sx(
                        js.Dynamic
                          .literal(
                            fontSize = 13,
                            color = "#5A6A65",
                            marginTop = "2px",
                            fontVariantNumeric = "tabular-nums"
                          )
                          .asInstanceOf[CSSPropertiesUtils]
                      )
                  )
              )
          }*
        )
      )
  }
}

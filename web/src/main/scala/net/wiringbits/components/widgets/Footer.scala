package net.wiringbits.components.widgets

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.*

import scala.scalajs.js

object Footer {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  private val footerBg = "#0E1B17"
  private val footerText = "#D5DEDB"
  private val footerSub = "#83958F"

  private val footerSx = js.Dynamic.literal(
    background = footerBg,
    marginTop = "auto",
    paddingTop = "48px",
    paddingBottom = "24px"
  )

  private val gridSx = js.Dynamic.literal(
    display = "grid",
    gridTemplateColumns = "1.6fr 1fr 1fr",
    gap = "48px"
  )

  private val gridMobileSx = js.Dynamic.literal(
    display = "flex",
    flexDirection = "column",
    gap = "40px"
  )

  private val sectionHeadSx = js.Dynamic.literal(
    color = "#FFFFFF",
    fontWeight = 700,
    fontSize = 13,
    letterSpacing = "0.1em",
    textTransform = "uppercase",
    marginBottom = "16px"
  )

  private val linkSx = js.Dynamic.literal(
    color = footerText,
    fontSize = 14,
    textDecoration = "none",
    display = "block",
    marginBottom = "8px"
  )

  private val subLabelSx = js.Dynamic.literal(
    color = footerSub,
    fontSize = 13,
    marginBottom = "4px"
  )

  private val bottomBarSx = js.Dynamic.literal(
    marginTop = "48px",
    paddingTop = "24px",
    borderTop = "1px solid rgba(255,255,255,0.08)",
    display = "flex",
    flexWrap = "wrap",
    gap = "12px",
    justifyContent = "space-between",
    alignItems = "center"
  )

  private val logoSx = new CSSPropertiesUtils {
    height = "28px"
    width = "auto"
    display = "block"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    def sectionHead(text: String) =
      mui.Typography(text).sx(sectionHeadSx.asInstanceOf[CSSPropertiesUtils])

    def footerLink(text: String, href: String) =
      a(
        slinky.web.html.href := href,
        target := "_blank",
        rel := "noopener noreferrer",
        style := new CSSPropertiesUtils {
          color = footerText
          fontSize = "14px"
          textDecoration = "none"
          display = "block"
          marginBottom = "8px"
        }
      )(text)

    val brand = mui.Box()(
      mui
        .Box()
        .sx(
          js.Dynamic
            .literal(display = "flex", alignItems = "center", gap = "10px", marginBottom = "16px")
            .asInstanceOf[CSSPropertiesUtils]
        )(
          img(src := "/img/wiringbits-logo.png", alt := "wiringbits", style := logoSx),
          mui
            .Typography("Wiringbits Web App Template")
            .sx(
              js.Dynamic
                .literal(color = "#FFFFFF", fontWeight = 800, fontSize = 18, letterSpacing = "-0.02em")
                .asInstanceOf[CSSPropertiesUtils]
            )
        ),
      mui
        .Typography(
          "While wiringbits is a company based in Culiacan, Mexico, there is no office — everyone works remotely. We strive for great quality on the software we build, and try to open source everything we can."
        )
        .sx(
          js.Dynamic
            .literal(color = footerSub, fontSize = 14.5, lineHeight = 1.7, maxWidth = 440)
            .asInstanceOf[CSSPropertiesUtils]
        )
    )

    val projects = mui.Box()(
      sectionHead("Projects"),
      footerLink("CollabUML", "https://collabuml.com"),
      footerLink("The Stakenet Block Explorer", "https://xsnexplorer.io/"),
      footerLink("The Stakenet Orderbook", "https://orderbook.stakenet.io/XSN_BTC"),
      footerLink("Pull Request Attention", "https://prattention.com"),
      footerLink("CazaDescuentos", "https://cazadescuentos.net"),
      footerLink("safer.chat", "https://safer.chat"),
      footerLink("Crypto Coin Alerts", "https://github.com/AlexITC/crypto-coin-alerts")
    )

    val contact = mui.Box()(
      sectionHead("Contact"),
      mui
        .Box()
        .sx(js.Dynamic.literal(marginBottom = "16px").asInstanceOf[CSSPropertiesUtils])(
          mui.Typography("Email").sx(subLabelSx.asInstanceOf[CSSPropertiesUtils]),
          a(
            slinky.web.html.href := s"mailto:${props.ctx.contactEmail.string}",
            style := new CSSPropertiesUtils {
              color = footerText
              fontSize = "14px"
              textDecoration = "none"
            }
          )(props.ctx.contactEmail.string)
        ),
      mui.Box()(
        mui.Typography("Phone").sx(subLabelSx.asInstanceOf[CSSPropertiesUtils]),
        mui
          .Typography(props.ctx.contactPhone)
          .sx(
            js.Dynamic.literal(color = footerText, fontSize = 14).asInstanceOf[CSSPropertiesUtils]
          )
      )
    )

    val bottomBar = mui
      .Box()
      .sx(bottomBarSx.asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography(s"Wiringbits Web App Template · 2026")
          .sx(
            js.Dynamic.literal(color = footerSub, fontSize = 13).asInstanceOf[CSSPropertiesUtils]
          ),
        a(
          slinky.web.html.href := "https://github.com/wiringbits/scala-webapp-template",
          target := "_blank",
          rel := "noopener noreferrer",
          style := new CSSPropertiesUtils {
            color = footerSub
            fontSize = "13px"
            textDecoration = "none"
          }
        )("Open source on GitHub")
      )

    mui
      .Box()
      .component("footer")
      .sx(footerSx.asInstanceOf[CSSPropertiesUtils])(
        mui.Container.maxWidth("lg")(
          mui
            .Box()
            .sx(gridSx.asInstanceOf[CSSPropertiesUtils])(
              brand,
              projects,
              contact
            ),
          bottomBar
        )
      )
  }
}

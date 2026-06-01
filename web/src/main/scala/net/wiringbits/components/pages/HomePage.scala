package net.wiringbits.components.pages

import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.AppContext
import net.wiringbits.core.{I18nHooks, ReactiveHooks}
import net.wiringbits.models.AuthState
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.useHistory
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.*

import scala.scalajs.js

object HomePage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  private val primary = "#0E8C6F"
  private val primaryAlpha10 = "rgba(14,140,111,0.10)"
  private val divider = "#E6ECEA"

  private val CODE_SAMPLE =
    """AsyncComponent.component[GetUserLogs.Response](
  AsyncComponent.Props(
    fetch  = () => props.ctx.api.client.getUserLogs(),
    render = response =>
      LogList.component(LogList.Props(props.ctx, response))
  )
)"""

  private case class Feature(icon: ReactElement, title: String, body: String)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()
    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
    val isAuthed = auth match { case _: AuthState.Authenticated => true; case _ => false }

    // ---- Hero ----
    val heroBadge = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            display = "inline-flex",
            alignItems = "center",
            gap = "8px",
            px = "12px",
            py = "5px",
            borderRadius = "999px",
            border = s"1px solid $divider",
            bgcolor = "#FFFFFF",
            color = "#5A6A65",
            fontSize = 13,
            fontWeight = 600,
            marginBottom = "24px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Icon(muiIcons.Bolt())
          .sx(js.Dynamic.literal(color = primary, fontSize = 16).asInstanceOf[CSSPropertiesUtils]),
        "Scala / Scala.js skeleton"
      )

    val heroTitle = mui
      .Typography()
      .sx(
        js.Dynamic
          .literal(
            fontSize = 56,
            fontWeight = 800,
            letterSpacing = "-0.025em",
            lineHeight = 1.05,
            marginBottom = "20px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        "Ship web apps, ",
        span(style := new CSSPropertiesUtils { color = primary })("not boilerplate")
      )

    val heroSubtitle = mui
      .Typography(
        "A reusable skeleton to build web applications in Scala / Scala.js — user registration, login, profiles and deployments, ready out of the box."
      )
      .sx(
        js.Dynamic
          .literal(
            fontSize = 19,
            color = "#5A6A65",
            maxWidth = 620,
            margin = "0 auto",
            lineHeight = 1.6,
            marginBottom = "36px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )

    val heroButtons = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(display = "flex", gap = "12px", justifyContent = "center", flexWrap = "wrap")
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui.Button
          .normal()(if (isAuthed) "Go to dashboard" else "Get started")
          .variant("contained")
          .size("large")
          .endIcon(mui.Icon(muiIcons.ArrowForward()))
          .onClick(_ => history.push(if (isAuthed) "/dashboard" else "/signup")),
        a(
          slinky.web.html.href := "https://github.com/wiringbits/scala-webapp-template",
          target := "_blank",
          rel := "noopener noreferrer",
          style := new CSSPropertiesUtils { textDecoration = "none" }
        )(
          mui.Button
            .normal()("View repository")
            .variant("outlined")
            .size("large")
            .color("inherit")
            .sx(js.Dynamic.literal(color = "#15201D", borderColor = divider).asInstanceOf[CSSPropertiesUtils])
            .startIcon(mui.Icon(muiIcons.GitHub()))
        )
      )

    val hero = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            position = "relative",
            overflow = "hidden",
            borderBottom = s"1px solid $divider",
            background = s"radial-gradient(1200px 380px at 50% -120px, ${primaryAlpha10}, transparent)"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui.Container.maxWidth("md")(
          mui
            .Box()
            .sx(
              js.Dynamic
                .literal(textAlign = "center", paddingTop = "96px", paddingBottom = "96px")
                .asInstanceOf[CSSPropertiesUtils]
            )(heroBadge, heroTitle, heroSubtitle, heroButtons)
        )
      )

    // ---- Feature cards ----
    def featureCard(icon: ReactElement, title: String, body: String): ReactElement =
      mui
        .Card()
        .variant("outlined")
        .sx(
          js.Dynamic.literal(borderRadius = "12px", borderColor = divider).asInstanceOf[CSSPropertiesUtils]
        )(
          mui
            .CardContent()
            .sx(js.Dynamic.literal(padding = "24px").asInstanceOf[CSSPropertiesUtils])(
              mui
                .Box()
                .sx(
                  js.Dynamic
                    .literal(
                      width = 44,
                      height = 44,
                      borderRadius = "8px",
                      display = "flex",
                      alignItems = "center",
                      justifyContent = "center",
                      marginBottom = "16px",
                      bgcolor = primaryAlpha10,
                      color = primary
                    )
                    .asInstanceOf[CSSPropertiesUtils]
                )(icon),
              mui
                .Typography(title)
                .variant("h6")
                .sx(
                  js.Dynamic.literal(marginBottom = "8px", fontSize = 18).asInstanceOf[CSSPropertiesUtils]
                ),
              mui
                .Typography(body)
                .sx(
                  js.Dynamic
                    .literal(color = "#5A6A65", fontSize = 14.5, lineHeight = 1.6)
                    .asInstanceOf[CSSPropertiesUtils]
                )
            )
        )

    val features = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            display = "grid",
            gridTemplateColumns = "1fr 1fr",
            gap = "20px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        featureCard(
          mui.Icon(muiIcons.Person()).sx(js.Dynamic.literal(fontSize = 23).asInstanceOf[CSSPropertiesUtils]),
          "User profile",
          "Everything you need to create accounts, change passwords and update profiles — already wired up and tested."
        ),
        featureCard(
          mui.Icon(muiIcons.Hub()).sx(js.Dynamic.literal(fontSize = 23).asInstanceOf[CSSPropertiesUtils]),
          "Swagger integration",
          "The boilerplate to expose your application's API through Swagger ships in the box, ready to explore."
        ),
        featureCard(
          mui.Icon(muiIcons.Refresh()).sx(js.Dynamic.literal(fontSize = 23).asInstanceOf[CSSPropertiesUtils]),
          "Consistent data loading",
          "A single AsyncComponent handles loading, error and retry states the same way across every screen."
        ),
        featureCard(
          mui.Icon(muiIcons.Security()).sx(js.Dynamic.literal(fontSize = 23).asInstanceOf[CSSPropertiesUtils]),
          "Tests as first-class citizens",
          "GitHub Actions and a deep integration-test suite mean onboarding new developers takes little effort."
        )
      )

    // ---- Swagger section ----
    val swaggerPreviewTopBar = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            bgcolor = "#0C1311",
            px = "20px",
            py = "14px",
            display = "flex",
            alignItems = "center",
            gap = "12px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                width = 22,
                height = 22,
                borderRadius = "50%",
                bgcolor = "#86C440",
                display = "flex",
                alignItems = "center",
                justifyContent = "center",
                color = "#0E1B17"
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(mui.Icon(muiIcons.Hub()).sx(js.Dynamic.literal(fontSize = 14).asInstanceOf[CSSPropertiesUtils])),
        mui
          .Typography("Swagger")
          .sx(
            js.Dynamic.literal(color = "#FFFFFF", fontWeight = 700, fontSize = 14.5).asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(flex = 1, mx = "8px", height = 30, borderRadius = "4px", bgcolor = "#FFFFFF")
              .asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                px = "14px",
                py = "5px",
                borderRadius = "4px",
                bgcolor = "#86C440",
                color = "#0E1B17",
                fontWeight = 700,
                fontSize = 12.5
              )
              .asInstanceOf[CSSPropertiesUtils]
          )("Explore")
      )

    def swaggerEndpoint(method: String, methodBg: String, methodFg: String, path: String, desc: String): ReactElement =
      mui
        .Box()
        .sx(
          js.Dynamic
            .literal(
              display = "flex",
              alignItems = "center",
              gap = "12px",
              border = s"1px solid ${methodFg}44",
              borderRadius = "6px",
              bgcolor = s"${methodFg}18",
              px = "10px",
              py = "8px"
            )
            .asInstanceOf[CSSPropertiesUtils]
        )(
          mui
            .Box()
            .sx(
              js.Dynamic
                .literal(
                  width = 58,
                  textAlign = "center",
                  py = "4px",
                  borderRadius = "4px",
                  bgcolor = methodFg,
                  color = "#FFFFFF",
                  fontWeight = 700,
                  fontSize = 12,
                  letterSpacing = "0.03em"
                )
                .asInstanceOf[CSSPropertiesUtils]
            )(method),
          mui
            .Typography(path)
            .sx(
              js.Dynamic
                .literal(
                  fontFamily = """ui-monospace, SFMono-Regular, Menlo, monospace""",
                  fontWeight = 700,
                  fontSize = 14,
                  color = "#1B2B26"
                )
                .asInstanceOf[CSSPropertiesUtils]
            ),
          mui
            .Typography(desc)
            .sx(
              js.Dynamic.literal(color = "#5A6A65", fontSize = 13, flex = 1).asInstanceOf[CSSPropertiesUtils]
            )
        )

    val swaggerPreview = mui
      .Card()
      .variant("outlined")
      .sx(
        js.Dynamic
          .literal(borderRadius = "10px", overflow = "hidden", borderColor = divider)
          .asInstanceOf[CSSPropertiesUtils]
      )(
        swaggerPreviewTopBar,
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(px = "20px", py = "16px", borderBottom = s"1px solid $divider")
              .asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(display = "flex", alignItems = "center", gap = "8px", marginBottom = "4px")
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                mui
                  .Typography("Scala webapp template's API")
                  .sx(
                    js.Dynamic
                      .literal(fontWeight = 800, fontSize = 18, letterSpacing = "-0.01em")
                      .asInstanceOf[CSSPropertiesUtils]
                  ),
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(
                        px = "7px",
                        py = "2px",
                        borderRadius = "999px",
                        bgcolor = primaryAlpha10,
                        color = primary,
                        fontSize = 11,
                        fontWeight = 700
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )("beta")
              ),
            mui
              .Typography("Base URL: /api · The API for the Scala webapp template app")
              .sx(
                js.Dynamic.literal(color = "#5A6A65", fontSize = 13).asInstanceOf[CSSPropertiesUtils]
              )
          ),
        mui
          .Box()
          .sx(js.Dynamic.literal(px = "20px", py = "16px").asInstanceOf[CSSPropertiesUtils])(
            mui
              .Typography("Auth")
              .variant("overline")
              .sx(
                js.Dynamic
                  .literal(color = "#5A6A65", display = "block", marginBottom = "8px")
                  .asInstanceOf[CSSPropertiesUtils]
              ),
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(display = "flex", flexDirection = "column", gap = "10px")
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                swaggerEndpoint("GET", "", "#3B82F6", "/auth/me", "Get the details for the authenticated user"),
                swaggerEndpoint("POST", "", "#1E9E5A", "/auth/login", "Log into the app"),
                swaggerEndpoint("POST", "", "#1E9E5A", "/auth/logout", "Log out from the app"),
                swaggerEndpoint("GET", "", "#3B82F6", "/health", "Queries the application's health")
              )
          )
      )

    val swaggerSection = mui
      .Box()
      .sx(js.Dynamic.literal(marginTop = "48px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography("Swagger integration")
          .variant("overline")
          .sx(js.Dynamic.literal(color = "#5A6A65").asInstanceOf[CSSPropertiesUtils]),
        mui
          .Typography("Your API, ready to explore")
          .variant("h4")
          .sx(
            js.Dynamic.literal(fontSize = 30, marginTop = "4px", marginBottom = "8px").asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Typography(
            "The template ships the boilerplate to expose your application's API through Swagger — every endpoint documented and testable out of the box."
          )
          .sx(
            js.Dynamic
              .literal(color = "#5A6A65", fontSize = 15.5, lineHeight = 1.6, maxWidth = 640, marginBottom = "24px")
              .asInstanceOf[CSSPropertiesUtils]
          ),
        swaggerPreview
      )

    // ---- Code sample + states ----
    val codeCard = mui
      .Card()
      .variant("outlined")
      .sx(
        js.Dynamic
          .literal(borderRadius = "10px", overflow = "hidden", borderColor = divider)
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                px = "20px",
                py = "12px",
                borderBottom = s"1px solid $divider",
                display = "flex",
                alignItems = "center",
                gap = "10px"
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .Icon(muiIcons.Code())
              .sx(js.Dynamic.literal(color = "#5A6A65", fontSize = 19).asInstanceOf[CSSPropertiesUtils]),
            mui
              .Typography("AsyncComponent.scala")
              .sx(
                js.Dynamic
                  .literal(
                    fontWeight = 700,
                    fontSize = 14.5,
                    fontFamily = "ui-monospace, SFMono-Regular, Menlo, monospace"
                  )
                  .asInstanceOf[CSSPropertiesUtils]
              )
          ),
        pre(style := new CSSPropertiesUtils {
          margin = "0"
          padding = "20px"
          backgroundColor = "#0E1B17"
          color = "#CFE7DD"
          fontFamily = "ui-monospace, SFMono-Regular, Menlo, monospace"
          fontSize = "13px"
          lineHeight = "1.7"
          overflowX = "auto"
        })(CODE_SAMPLE)
      )

    val statesCard = mui
      .Card()
      .variant("outlined")
      .sx(
        js.Dynamic.literal(borderRadius = "10px", borderColor = divider).asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .CardContent()
          .sx(js.Dynamic.literal(padding = "24px").asInstanceOf[CSSPropertiesUtils])(
            mui
              .Typography("Built-in states")
              .variant("overline")
              .sx(
                js.Dynamic
                  .literal(color = "#5A6A65", display = "block", marginBottom = "16px")
                  .asInstanceOf[CSSPropertiesUtils]
              ),
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(display = "flex", alignItems = "center", gap = "12px", marginBottom = "16px")
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                mui
                  .CircularProgress()
                  .sx(
                    js.Dynamic
                      .literal(color = primary, width = "32px !important", height = "32px !important")
                      .asInstanceOf[CSSPropertiesUtils]
                  ),
                mui.Box()(
                  mui
                    .Typography("Loading")
                    .sx(js.Dynamic.literal(fontWeight = 600, fontSize = 14.5).asInstanceOf[CSSPropertiesUtils]),
                  mui
                    .Typography("while data is fetched")
                    .sx(js.Dynamic.literal(color = "#5A6A65", fontSize = 13).asInstanceOf[CSSPropertiesUtils])
                )
              ),
            mui.Divider(),
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(display = "flex", alignItems = "center", gap = "12px", marginTop = "16px")
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                mui
                  .Icon(muiIcons.Warning())
                  .sx(js.Dynamic.literal(fontSize = 34, color = "#C0392B").asInstanceOf[CSSPropertiesUtils]),
                mui
                  .Box()
                  .sx(js.Dynamic.literal(flex = 1).asInstanceOf[CSSPropertiesUtils])(
                    mui
                      .Typography("Error, with retry")
                      .sx(js.Dynamic.literal(fontWeight = 600, fontSize = 14.5).asInstanceOf[CSSPropertiesUtils]),
                    mui
                      .Typography("recover from a failed request")
                      .sx(js.Dynamic.literal(color = "#5A6A65", fontSize = 13).asInstanceOf[CSSPropertiesUtils])
                  ),
                mui.Button.normal()("Retry").variant("outlined").size("small").color("inherit")
              )
          )
      )

    val codeSection = mui
      .Box()
      .sx(js.Dynamic.literal(marginTop = "48px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography("Consistent data loading")
          .variant("overline")
          .sx(js.Dynamic.literal(color = "#5A6A65").asInstanceOf[CSSPropertiesUtils]),
        mui
          .Typography("Loading, error and retry — handled once")
          .variant("h4")
          .sx(
            js.Dynamic.literal(fontSize = 30, marginTop = "4px", marginBottom = "24px").asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(display = "grid", gridTemplateColumns = "1.25fr 1fr", gap = "20px", alignItems = "stretch")
              .asInstanceOf[CSSPropertiesUtils]
          )(codeCard, statesCard)
      )

    // ---- Testing strip ----
    val ciChecks = List(
      "Server unit & integration tests",
      "Web client build & tests",
      "End-to-end suite (API + database)",
      "Linting & formatting"
    )

    val testingStrip = mui
      .Card()
      .variant("outlined")
      .sx(
        js.Dynamic
          .literal(borderRadius = "10px", borderColor = divider, overflow = "hidden")
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(
            js.Dynamic.literal(display = "grid", gridTemplateColumns = "1fr 1fr").asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .Box()
              .sx(js.Dynamic.literal(padding = "32px").asInstanceOf[CSSPropertiesUtils])(
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(
                        width = 44,
                        height = 44,
                        borderRadius = "8px",
                        display = "flex",
                        alignItems = "center",
                        justifyContent = "center",
                        marginBottom = "16px",
                        bgcolor = primaryAlpha10,
                        color = primary
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    mui.Icon(muiIcons.Security()).sx(js.Dynamic.literal(fontSize = 23).asInstanceOf[CSSPropertiesUtils])
                  ),
                mui
                  .Typography("Tests are first-class citizens")
                  .variant("h5")
                  .sx(
                    js.Dynamic.literal(fontSize = 22, marginBottom = "8px").asInstanceOf[CSSPropertiesUtils]
                  ),
                mui
                  .Typography(
                    "A GitHub Actions pipeline and a deep integration-test suite make sure your APIs and database behave the way you expect. Clear, layered architecture means onboarding new developers takes little effort."
                  )
                  .sx(
                    js.Dynamic
                      .literal(color = "#5A6A65", fontSize = 15, lineHeight = 1.7)
                      .asInstanceOf[CSSPropertiesUtils]
                  )
              ),
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(
                    padding = "32px",
                    bgcolor = "#F7FAF9",
                    borderLeft = s"1px solid $divider"
                  )
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(display = "flex", alignItems = "center", gap = "8px", marginBottom = "16px")
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    mui
                      .Icon(muiIcons.GitHub())
                      .sx(js.Dynamic.literal(fontSize = 19, color = "#5A6A65").asInstanceOf[CSSPropertiesUtils]),
                    mui
                      .Typography("CI / GitHub Actions")
                      .sx(js.Dynamic.literal(fontWeight = 700, fontSize = 14).asInstanceOf[CSSPropertiesUtils]),
                    mui
                      .Box()
                      .sx(
                        js.Dynamic
                          .literal(
                            ml = "auto",
                            px = "8px",
                            py = "2px",
                            borderRadius = "999px",
                            bgcolor = "rgba(14,140,111,0.15)",
                            color = primary,
                            fontSize = 12,
                            fontWeight = 700
                          )
                          .asInstanceOf[CSSPropertiesUtils]
                      )("passing")
                  ),
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(display = "flex", flexDirection = "column", gap = "10px")
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    ciChecks.zipWithIndex.map { case (check, i) =>
                      mui
                        .Box()
                        .sx(
                          js.Dynamic
                            .literal(display = "flex", alignItems = "center", gap = "10px")
                            .asInstanceOf[CSSPropertiesUtils]
                        )
                        .withKey(i.toString)(
                          mui
                            .Icon(muiIcons.CheckCircle())
                            .sx(js.Dynamic.literal(fontSize = 19, color = primary).asInstanceOf[CSSPropertiesUtils]),
                          mui.Typography(check).sx(js.Dynamic.literal(fontSize = 14).asInstanceOf[CSSPropertiesUtils])
                        )
                    }*
                  )
              )
          )
      )

    val qualitySection = mui
      .Box()
      .sx(js.Dynamic.literal(marginTop = "48px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography("Quality")
          .variant("overline")
          .sx(js.Dynamic.literal(color = "#5A6A65").asInstanceOf[CSSPropertiesUtils]),
        mui
          .Typography("A simple architecture, well tested")
          .variant("h4")
          .sx(
            js.Dynamic.literal(fontSize = 30, marginTop = "4px", marginBottom = "24px").asInstanceOf[CSSPropertiesUtils]
          ),
        testingStrip
      )

    Fragment(
      hero,
      mui.Container.maxWidth("lg")(
        mui
          .Box()
          .sx(js.Dynamic.literal(paddingTop = "48px", paddingBottom = "64px").asInstanceOf[CSSPropertiesUtils])(
            features,
            swaggerSection,
            codeSection,
            qualitySection
          )
      )
    )
  }
}

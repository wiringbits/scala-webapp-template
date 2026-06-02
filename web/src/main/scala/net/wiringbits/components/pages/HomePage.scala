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

  private val CODE_SAMPLE =
    """AsyncComponent.component[GetUserLogs.Response](
  AsyncComponent.Props(
    fetch  = () => props.ctx.api.client.getUserLogs(),
    render = response =>
      LogList.component(LogList.Props(props.ctx, response))
  )
)"""

  private val SHIPS = List(
    "User registration with email verification",
    "Sign in / sign out / password reset",
    "User profile editing",
    "Transactional emails",
    "Background tasks & scheduled jobs",
    "Swagger / OpenAPI explorer",
    "GitHub Actions CI pipeline",
    "Docker deployment config",
    "Integration test suite",
    "Internationalisation (i18n)",
    "Activity log",
    "PostgreSQL data layer"
  )

  private val AVOIDED = List(
    (
      "No API contract drift",
      "Client and server share one set of models, so a renamed field can't silently break the frontend."
    ),
    (
      "No runtime type errors",
      "The compiler rejects mismatched data before it ever reaches a user — no \"undefined is not a function\"."
    ),
    (
      "No hand-written SQL mapping bugs",
      "A typed data layer over PostgreSQL maps rows to models for you, so column typos fail at compile time."
    ),
    (
      "No forgotten auth checks",
      "Authentication is wired into the request pipeline, not copy-pasted per endpoint where it's easy to miss."
    ),
    ("No config drift", "Typed configuration means a missing or malformed setting stops the build, not production."),
    (
      "No flaky onboarding",
      "Every screen and endpoint follows the same shape, so new developers — and AI — know where everything goes."
    )
  )

  private val TECH_STACK = List("Scala", "Scala.js", "Play Framework", "PostgreSQL", "React", "MUI 5")

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = useHistory()
    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
    val isAuthed = auth match { case _: AuthState.Authenticated => true; case _ => false }

    val aiPoints = List(
      (
        muiIcons.Security(),
        "Strong types guide AI",
        "The compiler catches mistakes before you even run the code, so AI suggestions stay grounded in what actually compiles."
      ),
      (
        muiIcons.Refresh(),
        "Consistent patterns",
        "Every endpoint looks the same and every page follows the same shape — AI learns the codebase and extends it reliably."
      ),
      (
        muiIcons.Hub(),
        "Swagger out of the box",
        "AI can read the full API contract directly, without digging through source code to understand your endpoints."
      ),
      (
        muiIcons.CheckCircle(),
        "Deep test suite",
        "AI can verify its own suggestions against a real integration suite — no manual checking required."
      )
    )
    val isDark = ReactiveHooks.useDistinctValue(props.ctx.$darkMode)

    val bgAlpha = if (isDark) "rgba(47,207,166,0.22)" else "rgba(14,140,111,0.10)"
    val iconBgAlpha = if (isDark) "rgba(47,207,166,0.16)" else "rgba(14,140,111,0.10)"
    val aiFriendlyBg =
      if (isDark) "linear-gradient(160deg, rgba(47,207,166,0.16), rgba(47,207,166,0.04))"
      else "linear-gradient(160deg, rgba(14,140,111,0.08), rgba(14,140,111,0.02))"
    val aiFriendlyBorder =
      if (isDark) "1px solid rgba(47,207,166,0.30)" else "1px solid rgba(14,140,111,0.30)"

    // ---- Hero ----
    val heroBadge = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            display = "inline-flex",
            alignItems = "center",
            gap = "6px",
            px = "12px",
            py = "5px",
            borderRadius = "999px",
            bgcolor = "primary.main",
            color = "primary.contrastText",
            fontSize = 13,
            fontWeight = 700,
            marginBottom = "24px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Icon(muiIcons.AutoAwesome())
          .sx(js.Dynamic.literal(fontSize = 15).asInstanceOf[CSSPropertiesUtils]),
        "AI-first full-stack Scala template"
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
        mui
          .Box()
          .component("span")
          .sx(js.Dynamic.literal(color = "primary.main").asInstanceOf[CSSPropertiesUtils])("not boilerplate")
      )

    val heroSubtitle = mui
      .Typography(
        "A typed, full-stack skeleton built so AI can extend it safely and a whole class of bugs never happens. Registration, login, profiles, emails, background tasks and CI/CD — ready out of the box."
      )
      .sx(
        js.Dynamic
          .literal(
            fontSize = 19,
            color = "text.secondary",
            maxWidth = 660,
            margin = "0 auto",
            lineHeight = 1.6,
            marginBottom = "24px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )

    val techStackPills = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            display = "flex",
            flexWrap = "wrap",
            gap = "8px",
            justifyContent = "center",
            marginBottom = "24px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        TECH_STACK.zipWithIndex.map { case (tech, i) =>
          mui
            .Box(tech)
            .sx(
              js.Dynamic
                .literal(
                  px = "11px",
                  py = "4px",
                  borderRadius = "999px",
                  border = 1,
                  bgcolor = "background.paper",
                  color = "text.secondary",
                  fontSize = 12.5,
                  fontWeight = 600,
                  fontFamily = "ui-monospace, SFMono-Regular, Menlo, monospace"
                )
                .asInstanceOf[CSSPropertiesUtils]
            )
            .withKey(i.toString)
        }*
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
            .sx(js.Dynamic.literal(color = "text.primary", borderColor = "divider").asInstanceOf[CSSPropertiesUtils])
            .startIcon(mui.Icon(muiIcons.GitHub()))
        )
      )

    val liveAppBadge = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            display = "flex",
            alignItems = "center",
            gap = "8px",
            justifyContent = "center",
            marginTop = "24px"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                width = 8,
                height = 8,
                borderRadius = "50%",
                bgcolor = "primary.main",
                boxShadow = s"0 0 0 4px ${iconBgAlpha}"
              )
              .asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Typography()
          .sx(
            js.Dynamic
              .literal(fontSize = 13.5, color = "text.secondary", fontWeight = 600)
              .asInstanceOf[CSSPropertiesUtils]
          )(
            "You're looking at the live app — ",
            mui
              .Box()
              .component("span")
              .sx(js.Dynamic.literal(color = "primary.main").asInstanceOf[CSSPropertiesUtils])("try it now.")
          )
      )

    val hero = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            position = "relative",
            overflow = "hidden",
            borderBottom = 1,
            background = s"radial-gradient(1200px 380px at 50% -120px, $bgAlpha, transparent)"
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
            )(heroBadge, heroTitle, heroSubtitle, techStackPills, heroButtons, liveAppBadge)
        )
      )

    // ---- AI-friendly section ----
    val aiFriendlySection = mui
      .Box()
      .sx(js.Dynamic.literal(marginBottom = "56px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                borderRadius = "24px",
                overflow = "hidden",
                border = aiFriendlyBorder,
                background = aiFriendlyBg
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .Box()
              .sx(js.Dynamic.literal(padding = "40px").asInstanceOf[CSSPropertiesUtils])(
                // badge
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(
                        display = "inline-flex",
                        alignItems = "center",
                        gap = "6px",
                        px = "10px",
                        py = "4px",
                        borderRadius = "999px",
                        bgcolor = "primary.main",
                        color = "primary.contrastText",
                        fontSize = 12,
                        fontWeight = 700,
                        marginBottom = "16px"
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    mui
                      .Icon(muiIcons.AutoAwesome())
                      .sx(js.Dynamic.literal(fontSize = 14).asInstanceOf[CSSPropertiesUtils]),
                    "AI-friendly by design"
                  ),
                // headline
                mui
                  .Typography()
                  .variant("h3")
                  .sx(
                    js.Dynamic
                      .literal(fontSize = 36, maxWidth = 720, marginBottom = "12px")
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    "Describe a feature. Let AI write the code. ",
                    mui
                      .Box()
                      .component("span")
                      .sx(js.Dynamic.literal(color = "primary.main").asInstanceOf[CSSPropertiesUtils])(
                        "Let the compiler tell you if it's right."
                      )
                  ),
                // subtitle
                mui
                  .Typography(
                    "Strong types, consistent patterns and a deep test suite make this a codebase that stays productive as AI tooling improves — not just a time-saver today."
                  )
                  .sx(
                    js.Dynamic
                      .literal(
                        fontSize = 16,
                        color = "text.secondary",
                        lineHeight = 1.7,
                        maxWidth = 660,
                        marginBottom = "32px"
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  ),
                // 4 points grid
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(
                        display = "grid",
                        gridTemplateColumns = js.Dynamic.literal(xs = "1fr", sm = "1fr 1fr"),
                        gap = "16px"
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    aiPoints.zipWithIndex.map { case ((icon, title, body), i) =>
                      mui
                        .Box()
                        .sx(
                          js.Dynamic
                            .literal(
                              display = "flex",
                              gap = "14px",
                              padding = "20px",
                              borderRadius = "10px",
                              bgcolor = "background.paper",
                              border = 1
                            )
                            .asInstanceOf[CSSPropertiesUtils]
                        )
                        .withKey(i.toString)(
                          mui
                            .Box()
                            .sx(
                              js.Dynamic
                                .literal(
                                  width = 40,
                                  height = 40,
                                  flexShrink = 0,
                                  borderRadius = "8px",
                                  display = "flex",
                                  alignItems = "center",
                                  justifyContent = "center",
                                  bgcolor = iconBgAlpha,
                                  color = "primary.main"
                                )
                                .asInstanceOf[CSSPropertiesUtils]
                            )(
                              mui.Icon(icon).sx(js.Dynamic.literal(fontSize = 21).asInstanceOf[CSSPropertiesUtils])
                            ),
                          mui.Box()(
                            mui
                              .Typography(title)
                              .sx(
                                js.Dynamic
                                  .literal(fontWeight = 700, fontSize = 15.5, marginBottom = "4px")
                                  .asInstanceOf[CSSPropertiesUtils]
                              ),
                            mui
                              .Typography(body)
                              .sx(
                                js.Dynamic
                                  .literal(fontSize = 14, color = "text.secondary", lineHeight = 1.6)
                                  .asInstanceOf[CSSPropertiesUtils]
                              )
                          )
                        )
                    }*
                  )
              )
          )
      )

    // ---- What ships ----
    val whatShipsSection = mui
      .Box()
      .sx(js.Dynamic.literal(marginBottom = "48px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography("What ships")
          .variant("overline")
          .sx(js.Dynamic.literal(color = "text.secondary").asInstanceOf[CSSPropertiesUtils]),
        mui
          .Typography("Everything that's already wired up")
          .variant("h4")
          .sx(
            js.Dynamic.literal(fontSize = 30, marginTop = "4px", marginBottom = "8px").asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Typography("No guessing what's included — here's what you get the moment you clone the repository.")
          .sx(
            js.Dynamic
              .literal(
                color = "text.secondary",
                fontSize = 15.5,
                lineHeight = 1.6,
                maxWidth = 640,
                marginBottom = "24px"
              )
              .asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                display = "grid",
                gridTemplateColumns = js.Dynamic.literal(xs = "1fr", sm = "1fr 1fr", md = "1fr 1fr 1fr"),
                gap = "10px"
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(
            SHIPS.zipWithIndex.map { case (item, i) =>
              mui
                .Box()
                .sx(
                  js.Dynamic
                    .literal(
                      display = "flex",
                      alignItems = "center",
                      gap = "12px",
                      padding = "14px",
                      borderRadius = "8px",
                      border = 1,
                      bgcolor = "background.paper"
                    )
                    .asInstanceOf[CSSPropertiesUtils]
                )
                .withKey(i.toString)(
                  mui
                    .Icon(muiIcons.CheckCircle())
                    .sx(
                      js.Dynamic
                        .literal(fontSize = 20, color = "primary.main", flexShrink = 0)
                        .asInstanceOf[CSSPropertiesUtils]
                    ),
                  mui
                    .Typography(item)
                    .sx(
                      js.Dynamic
                        .literal(fontSize = 14.5, fontWeight = 500, lineHeight = 1.4)
                        .asInstanceOf[CSSPropertiesUtils]
                    )
                )
            }*
          )
      )

    // ---- Feature cards ----
    def featureCard(icon: ReactElement, title: String, body: String): ReactElement =
      mui
        .Card()
        .variant("outlined")
        .sx(
          js.Dynamic.literal(borderRadius = "12px", borderColor = "divider").asInstanceOf[CSSPropertiesUtils]
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
                      bgcolor = iconBgAlpha,
                      color = "primary.main"
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
                    .literal(color = "text.secondary", fontSize = 14.5, lineHeight = 1.6)
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
            gridTemplateColumns = js.Dynamic.literal(xs = "1fr", sm = "1fr 1fr", md = "1fr 1fr 1fr"),
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
          mui.Icon(muiIcons.Email()).sx(js.Dynamic.literal(fontSize = 23).asInstanceOf[CSSPropertiesUtils]),
          "Transactional emails",
          "Verification, password-reset and notification emails are templated and sent through a pluggable mailer."
        ),
        featureCard(
          mui.Icon(muiIcons.Schedule()).sx(js.Dynamic.literal(fontSize = 23).asInstanceOf[CSSPropertiesUtils]),
          "Background tasks",
          "Scheduled jobs and async background work run reliably outside the request cycle, ready to extend."
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

    // ---- Full-stack in one language ----
    def stackBox(icon: ReactElement, title: String, sub: String, accent: Boolean): ReactElement = {
      val iconBoxBg = if (accent) "primary.main" else (if (isDark) "rgba(255,255,255,0.05)" else "#F1F4F3")
      val iconColor = if (accent) "primary.contrastText" else "text.secondary"
      mui
        .Box()
        .sx(
          js.Dynamic
            .literal(
              flex = 1,
              minWidth = 0,
              textAlign = "center",
              px = "16px",
              py = "24px",
              borderRadius = "10px",
              border = 1,
              borderColor = if (accent) "primary.main" else "divider",
              bgcolor = if (accent) iconBgAlpha else "background.paper"
            )
            .asInstanceOf[CSSPropertiesUtils]
        )(
          mui
            .Box()
            .sx(
              js.Dynamic
                .literal(
                  width = 46,
                  height = 46,
                  borderRadius = "8px",
                  margin = "0 auto 12px",
                  display = "flex",
                  alignItems = "center",
                  justifyContent = "center",
                  bgcolor = iconBoxBg,
                  color = iconColor
                )
                .asInstanceOf[CSSPropertiesUtils]
            )(
              mui.Icon(icon).sx(js.Dynamic.literal(fontSize = 24).asInstanceOf[CSSPropertiesUtils])
            ),
          mui
            .Typography(title)
            .sx(js.Dynamic.literal(fontWeight = 700, fontSize = 15.5).asInstanceOf[CSSPropertiesUtils]),
          mui
            .Typography(sub)
            .sx(
              js.Dynamic
                .literal(fontSize = 12.5, color = "text.secondary", marginTop = "3px")
                .asInstanceOf[CSSPropertiesUtils]
            )
        )
    }

    def syncArrow = mui
      .Box()
      .sx(
        js.Dynamic
          .literal(
            flexShrink = 0,
            display = "flex",
            alignItems = "center",
            color = "primary.main"
          )
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui.Icon(muiIcons.Sync()).sx(js.Dynamic.literal(fontSize = 22).asInstanceOf[CSSPropertiesUtils])
      )

    val fullStackSection = mui
      .Box()
      .sx(js.Dynamic.literal(marginTop = "48px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography("Full-stack in one language")
          .variant("overline")
          .sx(js.Dynamic.literal(color = "text.secondary").asInstanceOf[CSSPropertiesUtils]),
        mui
          .Typography("Write it once, share it everywhere")
          .variant("h4")
          .sx(
            js.Dynamic.literal(fontSize = 30, marginTop = "4px", marginBottom = "8px").asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Typography(
            "Server and client are written in the same language and share the same models — one of the template's most unusual properties."
          )
          .sx(
            js.Dynamic
              .literal(
                color = "text.secondary",
                fontSize = 15.5,
                lineHeight = 1.6,
                maxWidth = 640,
                marginBottom = "24px"
              )
              .asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Card()
          .variant("outlined")
          .sx(
            js.Dynamic.literal(borderRadius = "10px", borderColor = "divider").asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .CardContent()
              .sx(js.Dynamic.literal(padding = "32px").asInstanceOf[CSSPropertiesUtils])(
                mui
                  .Box()
                  .sx(
                    js.Dynamic
                      .literal(
                        display = "flex",
                        flexDirection = js.Dynamic.literal(xs = "column", md = "row"),
                        alignItems = "stretch",
                        gap = "8px"
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )(
                    stackBox(muiIcons.Storage(), "Server", "Play + PostgreSQL", false),
                    syncArrow,
                    stackBox(muiIcons.Layers(), "Shared models", "One source of truth", true),
                    syncArrow,
                    stackBox(muiIcons.Devices(), "Client", "Scala.js · React · MUI", false)
                  ),
                mui
                  .Typography(
                    "Server and client share the same models — no JSON contract drift, no duplicated types, no glue layer. One language to hire for, one codebase to maintain."
                  )
                  .sx(
                    js.Dynamic
                      .literal(
                        textAlign = "center",
                        color = "text.secondary",
                        fontSize = 14.5,
                        lineHeight = 1.7,
                        marginTop = "24px",
                        maxWidth = 640,
                        margin = "24px auto 0"
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )
              )
          )
      )

    // ---- Problems avoided ----
    val avoidedProblemsSection = mui
      .Box()
      .sx(js.Dynamic.literal(marginTop = "48px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography("Built to prevent bugs")
          .variant("overline")
          .sx(js.Dynamic.literal(color = "text.secondary").asInstanceOf[CSSPropertiesUtils]),
        mui
          .Typography("A whole class of problems, gone")
          .variant("h4")
          .sx(
            js.Dynamic.literal(fontSize = 30, marginTop = "4px", marginBottom = "8px").asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Typography(
            "Because of how the template is structured, entire categories of bugs simply can't happen — the compiler catches them long before your users do."
          )
          .sx(
            js.Dynamic
              .literal(
                color = "text.secondary",
                fontSize = 15.5,
                lineHeight = 1.6,
                maxWidth = 640,
                marginBottom = "24px"
              )
              .asInstanceOf[CSSPropertiesUtils]
          ),
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                display = "grid",
                gridTemplateColumns = js.Dynamic.literal(xs = "1fr", sm = "1fr 1fr", md = "1fr 1fr 1fr"),
                gap = "16px"
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(
            AVOIDED.zipWithIndex.map { case ((title, body), i) =>
              mui
                .Box()
                .sx(
                  js.Dynamic
                    .literal(
                      padding = "20px",
                      borderRadius = "10px",
                      border = 1,
                      bgcolor = "background.paper"
                    )
                    .asInstanceOf[CSSPropertiesUtils]
                )
                .withKey(i.toString)(
                  mui
                    .Box()
                    .sx(
                      js.Dynamic
                        .literal(
                          width = 34,
                          height = 34,
                          borderRadius = "50%",
                          display = "flex",
                          alignItems = "center",
                          justifyContent = "center",
                          marginBottom = "12px",
                          bgcolor = iconBgAlpha,
                          color = "primary.main"
                        )
                        .asInstanceOf[CSSPropertiesUtils]
                    )(
                      mui
                        .Icon(muiIcons.CheckCircle())
                        .sx(js.Dynamic.literal(fontSize = 18).asInstanceOf[CSSPropertiesUtils])
                    ),
                  mui
                    .Typography(title)
                    .sx(
                      js.Dynamic
                        .literal(fontWeight = 700, fontSize = 15.5, marginBottom = "4px")
                        .asInstanceOf[CSSPropertiesUtils]
                    ),
                  mui
                    .Typography(body)
                    .sx(
                      js.Dynamic
                        .literal(fontSize = 14, color = "text.secondary", lineHeight = 1.6)
                        .asInstanceOf[CSSPropertiesUtils]
                    )
                )
            }*
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

    def swaggerEndpoint(method: String, methodFg: String, path: String, desc: String): ReactElement =
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
                  color = "text.primary"
                )
                .asInstanceOf[CSSPropertiesUtils]
            ),
          mui
            .Typography(desc)
            .sx(
              js.Dynamic.literal(color = "text.secondary", fontSize = 13, flex = 1).asInstanceOf[CSSPropertiesUtils]
            )
        )

    val swaggerPreview = mui
      .Card()
      .variant("outlined")
      .sx(
        js.Dynamic
          .literal(borderRadius = "10px", overflow = "hidden", borderColor = "divider")
          .asInstanceOf[CSSPropertiesUtils]
      )(
        swaggerPreviewTopBar,
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(px = "20px", py = "16px", borderBottom = 1)
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
                        bgcolor = iconBgAlpha,
                        color = "primary.main",
                        fontSize = 11,
                        fontWeight = 700
                      )
                      .asInstanceOf[CSSPropertiesUtils]
                  )("beta")
              ),
            mui
              .Typography("Base URL: /api · The API for the Scala webapp template app")
              .sx(
                js.Dynamic.literal(color = "text.secondary", fontSize = 13).asInstanceOf[CSSPropertiesUtils]
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
                  .literal(color = "text.secondary", display = "block", marginBottom = "8px")
                  .asInstanceOf[CSSPropertiesUtils]
              ),
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(display = "flex", flexDirection = "column", gap = "10px")
                  .asInstanceOf[CSSPropertiesUtils]
              )(
                swaggerEndpoint("GET", "#3B82F6", "/auth/me", "Get the details for the authenticated user"),
                swaggerEndpoint("POST", "#1E9E5A", "/auth/login", "Log into the app"),
                swaggerEndpoint("POST", "#1E9E5A", "/auth/logout", "Log out from the app"),
                swaggerEndpoint("GET", "#3B82F6", "/health", "Queries the application's health")
              )
          )
      )

    val swaggerSection = mui
      .Box()
      .sx(js.Dynamic.literal(marginTop = "48px").asInstanceOf[CSSPropertiesUtils])(
        mui
          .Typography("Swagger integration")
          .variant("overline")
          .sx(js.Dynamic.literal(color = "text.secondary").asInstanceOf[CSSPropertiesUtils]),
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
              .literal(
                color = "text.secondary",
                fontSize = 15.5,
                lineHeight = 1.6,
                maxWidth = 640,
                marginBottom = "24px"
              )
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
          .literal(borderRadius = "10px", overflow = "hidden", borderColor = "divider")
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(
                px = "20px",
                py = "12px",
                borderBottom = 1,
                display = "flex",
                alignItems = "center",
                gap = "10px"
              )
              .asInstanceOf[CSSPropertiesUtils]
          )(
            mui
              .Icon(muiIcons.Code())
              .sx(js.Dynamic.literal(color = "text.secondary", fontSize = 19).asInstanceOf[CSSPropertiesUtils]),
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
        js.Dynamic.literal(borderRadius = "10px", borderColor = "divider").asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .CardContent()
          .sx(js.Dynamic.literal(padding = "24px").asInstanceOf[CSSPropertiesUtils])(
            mui
              .Typography("Built-in states")
              .variant("overline")
              .sx(
                js.Dynamic
                  .literal(color = "text.secondary", display = "block", marginBottom = "16px")
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
                      .literal(color = "primary.main", width = "32px !important", height = "32px !important")
                      .asInstanceOf[CSSPropertiesUtils]
                  ),
                mui.Box()(
                  mui
                    .Typography("Loading")
                    .sx(js.Dynamic.literal(fontWeight = 600, fontSize = 14.5).asInstanceOf[CSSPropertiesUtils]),
                  mui
                    .Typography("while data is fetched")
                    .sx(js.Dynamic.literal(color = "text.secondary", fontSize = 13).asInstanceOf[CSSPropertiesUtils])
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
                  .sx(js.Dynamic.literal(fontSize = 34, color = "error.main").asInstanceOf[CSSPropertiesUtils]),
                mui
                  .Box()
                  .sx(js.Dynamic.literal(flex = 1).asInstanceOf[CSSPropertiesUtils])(
                    mui
                      .Typography("Error, with retry")
                      .sx(js.Dynamic.literal(fontWeight = 600, fontSize = 14.5).asInstanceOf[CSSPropertiesUtils]),
                    mui
                      .Typography("recover from a failed request")
                      .sx(js.Dynamic.literal(color = "text.secondary", fontSize = 13).asInstanceOf[CSSPropertiesUtils])
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
          .sx(js.Dynamic.literal(color = "text.secondary").asInstanceOf[CSSPropertiesUtils]),
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
              .literal(
                display = "grid",
                gridTemplateColumns = js.Dynamic.literal(xs = "1fr", md = "1.25fr 1fr"),
                gap = "20px",
                alignItems = "stretch"
              )
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
          .literal(borderRadius = "10px", borderColor = "divider", overflow = "hidden")
          .asInstanceOf[CSSPropertiesUtils]
      )(
        mui
          .Box()
          .sx(
            js.Dynamic
              .literal(display = "grid", gridTemplateColumns = js.Dynamic.literal(xs = "1fr", md = "1fr 1fr"))
              .asInstanceOf[CSSPropertiesUtils]
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
                        bgcolor = iconBgAlpha,
                        color = "primary.main"
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
                      .literal(color = "text.secondary", fontSize = 15, lineHeight = 1.7)
                      .asInstanceOf[CSSPropertiesUtils]
                  )
              ),
            mui
              .Box()
              .sx(
                js.Dynamic
                  .literal(
                    padding = "32px",
                    bgcolor = "background.default",
                    borderLeft = 1
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
                      .sx(js.Dynamic.literal(fontSize = 19, color = "text.secondary").asInstanceOf[CSSPropertiesUtils]),
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
                            bgcolor = iconBgAlpha,
                            color = "primary.main",
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
                            .sx(
                              js.Dynamic.literal(fontSize = 19, color = "primary.main").asInstanceOf[CSSPropertiesUtils]
                            ),
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
          .sx(js.Dynamic.literal(color = "text.secondary").asInstanceOf[CSSPropertiesUtils]),
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
            aiFriendlySection,
            whatShipsSection,
            features,
            fullStackSection,
            avoidedProblemsSection,
            swaggerSection,
            codeSection,
            qualitySection
          )
      )
    )
  }
}

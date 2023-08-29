package net.wiringbits.components.pages

import com.olvind.mui.propTypes
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.csstype.mod.Property.{FlexDirection, TextAlign}
import net.wiringbits.AppContext
import net.wiringbits.api.models.VerifyEmail
import net.wiringbits.common.models.UserToken
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.{useHistory, useParams}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html.{className, div, style}

import scala.scalajs.js
import scala.scalajs.js.timers.setTimeout
import scala.util.{Failure, Success}

object VerifyEmailWithTokenPage {
  def apply(ctx: AppContext): KeyAddingStage =
    component(Props(ctx = ctx))

  case class Props(ctx: AppContext)

  private case class State(
      loading: Boolean,
      error: Option[String],
      title: String,
      message: String
  )

  val emailPageStyling: CSSProperties = new CSSProperties {
    flex = 1
    display = "flex"
    flexDirection = FlexDirection.column
    alignItems = "center"
    textAlign = TextAlign.center
    justifyContent = "center"
  }

  val emailTitleStyling: CSSProperties = new CSSProperties {
    fontWeight = 600
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)

    val initialState = State(
      loading = false,
      error = None,
      title = texts.verifyingEmail,
      message = texts.waitAMomentPlease
    )

    val history = useHistory()
    val params = useParams()
    val (state, setState) = Hooks.useState(initialState)
    val emailCodeOpt = UserToken.validate(params.get("emailCode").getOrElse(""))

    def sendEmailCode(): Unit = {
      setState(_.copy(loading = true))
      emailCodeOpt match {
        case Some(emailCode) =>
          props.ctx.api.client.verifyEmail(VerifyEmail.Request(emailCode)).onComplete {
            case Success(_) =>
              val title = texts.successfulEmailVerification
              val message = texts.goingToBeRedirected
              setState(_.copy(loading = false, title = title, message = message))
              setTimeout(2000) {
                history.push("/signin")
              }

            case Failure(ex) =>
              val title = texts.failedEmailVerification
              val message = ex.getMessage
              setState(_.copy(loading = false, title = title, message = message, error = Some(message)))
          }
        case None =>
          val title = texts.failedEmailVerification
          val message = texts.invalidVerificationToken
          setState(_.copy(loading = false, title = title, message = message, error = Some(message)))
      }
    }

    Hooks.useEffect(() => sendEmailCode(), "")

    val loading =
      if (state.loading || state.error.isEmpty)
        Fragment(
          loader
        )
      else {
        Fragment(
        )
      }

    div(className := "emailCodePage", style := emailPageStyling)(
      Fragment(
        mui.Typography(state.title).variant("h5").className("emailTitle").style(emailTitleStyling),
        mui.Typography(state.message).variant("h6"),
        loading
      )
    )
  }

  private def loader = Container(
    alignItems = Container.Alignment.center,
    justifyContent = Container.Alignment.center,
    padding = Container.EdgeInsets.vertical(16),
    child = Fragment(
      CircularLoader(50)
    )
  )
}

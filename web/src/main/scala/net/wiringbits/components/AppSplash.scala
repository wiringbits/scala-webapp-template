package net.wiringbits.components

import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.Alignment
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle, Title}
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks, ReactElement}

import scala.util.{Failure, Success}

@react object AppSplash {

  case class Props(ctx: AppContext, child: ReactElement)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val (initialized, setInitialized) = Hooks.useState(false)

    Hooks.useEffect(
      () => {
        // load language
        // TODO: It is ideal to detect the browser language when there is no language stored
        props.ctx.api.storage
          .findLang()
          .foreach(lang => props.ctx.$lang := lang)

        props.ctx.api.client.currentUser().onComplete {
          case Success(res) =>
            props.ctx.loggedIn(User(name = res.name, email = res.email))
            setInitialized(true)

          case Failure(ex) =>
            println(
              s"Failed to get current user, we are either unauthenticated or the server had a problem: ${ex.getMessage}"
            )
            setInitialized(true)
        }

      },
      ""
    )

    if (initialized) {
      Fragment(props.child)
    } else {
      Container(
        flex = Some(1),
        alignItems = Alignment.center,
        justifyContent = Alignment.center,
        child = Fragment(
          Title(texts.appName),
          Subtitle(texts.loading)
        )
      )
    }
  }
}

package net.wiringbits.components

import net.wiringbits.models.User
import net.wiringbits.ui.components.core.widgets.Container.Alignment
import net.wiringbits.ui.components.core.widgets.{Container, Subtitle, Title}
import net.wiringbits.{API, AppStrings}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks, ReactElement}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@react object AppSplash {
  case class Props(api: API, loggedIn: User => Unit, child: ReactElement)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (initialized, setInitialized) = Hooks.useState(false)

    Hooks.useEffect(
      () =>
        props.api.storage.findJwt.filter(_.nonEmpty) match {
          case Some(jwt) =>
            props.api.client.currentUser(jwt).onComplete {
              case Success(res) =>
                props.loggedIn(User(name = res.name, email = res.email, jwt = jwt))
                setInitialized(true)

              case Failure(ex) =>
                ex.printStackTrace()
                setInitialized(true)
            }
          case None =>
            setInitialized(true)
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
          Title(AppStrings.appName),
          Subtitle(AppStrings.loading)
        )
      )
    }
  }
}

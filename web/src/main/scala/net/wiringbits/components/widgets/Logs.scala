package net.wiringbits.components.widgets

import net.wiringbits.AppContext
import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.models.User
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import net.wiringbits.webapp.utils.slinkyUtils.core.GenericHooks
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object Logs {
  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (timesRefreshingData, forceRefresh) = GenericHooks.useForceRefresh

    AsyncComponent.component[GetUserLogs.Response](
      AsyncComponent.Props(
        fetch = () => props.ctx.api.client.getUserLogs(),
        render = response => LogList.component(LogList.Props(props.ctx, response, () => forceRefresh())),
        progressIndicator = () => Loader(props.ctx),
        watchedObjects = List(timesRefreshingData)
      )
    )
  }
}

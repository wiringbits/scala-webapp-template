package net.wiringbits.ui.components.core

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import slinky.core.FunctionalComponent
import slinky.core.facade.{Hooks, ReactElement}
import slinky.web.html._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * A reusable component to render some data that's retrieved from a remote source, providing:
 * - A progress indicator when the data is being retrieved.
 * - Invoking the render function when the data is available, to render such data.
 * - Displaying an error message when retrieving the data has failed, as well as displaying a
 *   retry button so that the user is able to try again.
 */
object RemoteDataLoader {

  sealed trait DataState[T] extends Product with Serializable {
    def loaded(data: T): DataState.Loaded[T] = DataState.Loaded(data)
    def failed(msg: String): DataState.Failed[T] = DataState.Failed(msg)
  }

  object DataState {
    final case class Loading[T]() extends DataState[T]
    final case class Loaded[T](data: T) extends DataState[T]
    final case class Failed[T](msg: String) extends DataState[T]

    def loading[T]: DataState[T] = Loading[T]()
  }

  /**
   * @param fetch the function to fetch the data
   * @param render the function to render the data once it is available
   * @param onDataLoaded a function invoked when the remote data has been loaded
   * @param progressIndicator the component rendered when the data is being loaded
   * @param progressIndicatorWhileReloadingData whether to display the progress indicator every time
   *                                            the data is being reloaded
   * @param retryLabel the label to use in the button that retries the operation
   * @param watchedObjects objects being watched, when any of those changes, the data is loaded again
   */
  case class Props[D](
      fetch: () => Future[D],
      render: D => ReactElement,
      onDataLoaded: D => Unit = (_: D) => (),
      progressIndicator: () => ReactElement = () => loader,
      progressIndicatorWhileReloadingData: Boolean = false,
      retryLabel: String = "Retry",
      watchedObjects: Iterable[Any] = List("")
  )

  /**
   * @tparam D The data to fetch and render
   * @return the component
   */
  def component[D]: FunctionalComponent[Props[D]] = FunctionalComponent[Props[D]] { props =>
    val (dataState, setDataState) = Hooks.useState[DataState[D]](DataState.loading[D])

    def reload(): Unit = {
      def f(state: DataState[D]): DataState[D] = state match {
        case _: DataState.Failed[D] => DataState.loading[D]
        case _ =>
          if (props.progressIndicatorWhileReloadingData) DataState.loading[D]
          else state
      }
      setDataState(f _)
      props.fetch().onComplete {
        case Success(value) =>
          setDataState(_.loaded(value))
          props.onDataLoaded(value)

        case Failure(ex) => setDataState(_.failed(ex.getMessage))
      }
    }

    Hooks.useEffect(reload _, props.watchedObjects)

    dataState match {
      case DataState.Loading() =>
        props.progressIndicator()

      case DataState.Loaded(data) =>
        props.render(data)

      case DataState.Failed(msg) =>
        error(msg, props, () => reload())
    }
  }

  private def loader: ReactElement = {
    div(
      mui.CircularProgress()
    )
  }

  private def error[D](msg: String, props: Props[D], reload: () => Unit): ReactElement = {
    div(
      mui
        .Typography()
        .color(com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color.secondary)(msg),
      mui.Button.onClick(_ => reload())(props.retryLabel)
    )
  }
}

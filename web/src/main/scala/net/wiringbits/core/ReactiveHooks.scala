package net.wiringbits.core

import monix.reactive.subjects.Var
import slinky.core.facade.Hooks

object ReactiveHooks {

  import monix.execution.Scheduler.Implicits.global

  /** Gets the value from a monix Var, and, updates the state when the Var gets new values
    */
  def useValue[T](value: Var[T]): T = {
    val (state, setState) = Hooks.useState[T](value())
    Hooks.useEffect(
      () => {
        val cancelable = value.foreach(setState.apply)
        () => cancelable.cancel()
      },
      List(value)
    )
    state
  }

  /** Gets the value from a monix Var, and, updates the state only when it gets a different value
    */
  def useDistinctValue[T](value: Var[T])(implicit A: cats.Eq[T]): T = {
    val (state, setState) = Hooks.useState[T](value())
    Hooks.useEffect(
      () => {
        val cancelable = value.distinctUntilChanged.foreach(setState.apply)
        () => cancelable.cancel()
      },
      List(value)
    )
    state
  }
}

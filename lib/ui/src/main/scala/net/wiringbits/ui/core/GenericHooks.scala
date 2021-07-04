package net.wiringbits.ui.core

import slinky.core.facade.Hooks

object GenericHooks {

  /**
   * A react hook to force a component refresh, use it with care.
   *
   * @return the times the component has been refreshed, and a function to force the refresh
   */
  def useForceRefresh: (Int, () => Unit) = {
    val (timesRefreshed, increment) = Hooks.useReducer[Int, Int](_ + _, 0)
    (timesRefreshed, () => increment(1))
  }
}

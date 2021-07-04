package net.wiringbits.ui.core

import com.alexitc.materialui.facade.materialUiCore.useMediaQueryMod.unstableUseMediaQuery

object MediaQueryHooks {

  def useIsLaptop() = {
    unstableUseMediaQuery("(min-width: 769px)")
  }

  def useIsTablet() = {
    unstableUseMediaQuery("(min-width: 426px) and (max-width: 768px)")
  }

  def useIsMobile() = {
    unstableUseMediaQuery("(max-width: 425px)")
  }

  def useIsMobileOrTablet() = {
    unstableUseMediaQuery("(max-width: 768px)")
  }

}

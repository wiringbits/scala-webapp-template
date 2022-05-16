package net.wiringbits.facades

import scala.annotation.unused
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

package object reactadmin {
  @js.native
  @JSImport("ra-data-simple-rest", JSImport.Default)
  def simpleRestProvider(@unused url: String): DataProvider = js.native
}

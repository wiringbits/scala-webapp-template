package net.wiringbits.util.models

import net.wiringbits.common.models.core.WrappedString

package object ordering {
  case class OrderingCondition(string: String) extends AnyVal with WrappedString
}

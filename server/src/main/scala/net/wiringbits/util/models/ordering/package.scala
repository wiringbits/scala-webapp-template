package net.wiringbits.util.models

package object ordering {
  case class OrderingCondition(string: String) extends AnyVal with WrappedString
}

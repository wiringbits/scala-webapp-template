package net.wiringbits.util.models

trait WrappedString extends Any {
  def string: String

  override def toString: String = string
}

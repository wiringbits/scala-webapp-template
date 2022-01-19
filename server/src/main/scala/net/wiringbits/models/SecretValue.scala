package net.wiringbits.models

import net.wiringbits.util.StringUtils.Implicits.StringUtilsExt

abstract class SecretValue(string: String) {
  override def toString: String = string.mask()
}

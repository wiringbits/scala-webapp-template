package net.wiringbits.util

object StringUtils {

  def mask(value: String, prefixSize: Int, suffixSize: Int): String = {
    if (value.length <= prefixSize + suffixSize + 4) {
      "..."
    } else {
      s"${value.take(prefixSize)}...${value.takeRight(suffixSize)}"
    }
  }

  object Implicits {

    implicit class StringUtilsExt(val string: String) extends AnyVal {
      def mask(prefix: Int = 2, suffix: Int = 2): String = StringUtils.mask(string, prefix, suffix)
    }
  }
}

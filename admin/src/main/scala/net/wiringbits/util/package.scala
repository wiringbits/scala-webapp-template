package net.wiringbits

package object util {

  private def upperCaseFirstLetter(word: String): String = {
    // This replace every ocurrence
    word.replace(word.head, word.head.toUpper)
  }

  def formatField(word: String): String = {
    val splittedArray = word.split("_")
    splittedArray.map(upperCaseFirstLetter).mkString(" ")
  }
}

package anorm

// Dummy to allow using anorm in our sjs compiled models
// Based on https://github.com/playframework/anorm
case class TypeDoesNotMatch(reason: String) extends SqlRequestError {
  lazy val message = ""
  override lazy val toString = ""
}

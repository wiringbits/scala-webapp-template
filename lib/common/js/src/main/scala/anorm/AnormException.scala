package anorm

// Dummy to allow using anorm in our sjs compiled models
// Based on https://github.com/playframework/anorm
final case class AnormException(message: String) extends Exception with scala.util.control.NoStackTrace {
  override def getMessage() = message
}

package anorm

final case class AnormException(message: String) extends Exception with scala.util.control.NoStackTrace {
  override def getMessage() = message
}

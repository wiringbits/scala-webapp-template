package anorm

// Dummy to allow using anorm in our sjs compiled models
// Based on https://github.com/playframework/anorm
object TimestampWrapper1 {
  def unapply(that: Any): Option[java.sql.Timestamp] = Option(1.asInstanceOf[java.sql.Timestamp])
}

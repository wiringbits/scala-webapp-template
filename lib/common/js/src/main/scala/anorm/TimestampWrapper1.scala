package anorm

object TimestampWrapper1 {
  def unapply(that: Any): Option[java.sql.Timestamp] = Option(1.asInstanceOf[java.sql.Timestamp])
}

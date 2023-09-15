package java.sql

// Dummy to allow using java.sql in our sjs compiled models
case class PreparedStatement() {
  def setObject(i: Int, value: Any): Unit = ()
}

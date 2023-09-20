package java.sql

// Dummy to allow using java.sql in our sjs compiled models
case class PreparedStatement() {
  def setObject(i: Int, x: Object): Unit = ()

  def setString(i: Int, x: String): Unit = ()

  def setInt(i: Int, x: Int): Unit = ()

  def setNull(i: Int, x: Int): Unit = ()
}

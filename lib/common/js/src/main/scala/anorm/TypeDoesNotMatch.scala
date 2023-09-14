package anorm

case class TypeDoesNotMatch(reason: String) extends SqlRequestError {
  lazy val message = ""
  override lazy val toString = ""
}

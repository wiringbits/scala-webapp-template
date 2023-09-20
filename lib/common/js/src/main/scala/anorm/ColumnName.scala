package anorm

// Dummy to allow using anorm in our sjs compiled models
// Based on https://github.com/playframework/anorm
case class ColumnName(qualified: String, alias: Option[String])

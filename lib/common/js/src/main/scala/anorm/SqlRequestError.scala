package anorm

import scala.util.Failure

// Dummy to allow using anorm in our sjs compiled models
// Based on https://github.com/playframework/anorm
trait SqlRequestError {
  def message: String

  def toFailure = Failure(AnormException(message))
}

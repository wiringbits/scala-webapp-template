package anorm

import scala.util.Failure

trait SqlRequestError {
  def message: String

  def toFailure = Failure(AnormException(message))
}

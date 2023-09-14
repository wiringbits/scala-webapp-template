package anorm

import java.sql.PreparedStatement
import java.time.Instant
import java.util.UUID

trait ToStatement[T] {
  def contramap[A](f: A => T): ToStatement[A] = new ToStatement[A] {}
}

object ToStatement {
  def apply[T](set: (PreparedStatement, Int, T) => Unit): ToStatement[T] = new ToStatement[T] {}

  def apply[T]: ToStatement[T] = new ToStatement[T] {}

  implicit object stringToStatement extends ToStatement[String] {
    def set(s: PreparedStatement, i: Int, str: String): Unit = ()
  }

  implicit def optionToStatement[A](implicit c: ToStatement[A], meta: ParameterMetaData[A]): ToStatement[Option[A]] =
    new ToStatement[Option[A]] {}

  implicit object intToStatement extends ToStatement[Int] {
    def set(s: PreparedStatement, i: Int, v: Int): Unit = (s.setInt(i, v))
  }

  implicit def instantToStatement(implicit meta: ParameterMetaData[Instant]): ToStatement[Instant] =
    new ToStatement[Instant] {}

  implicit object uuidToStatement extends ToStatement[UUID] {
    val jdbcType = implicitly[ParameterMetaData[UUID]].jdbcType

    def set(s: PreparedStatement, index: Int, id: UUID): Unit =
      if (id != (null: UUID)) s.setString(index, id.toString)
      else s.setNull(index, jdbcType)
  }
}

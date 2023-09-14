package anorm

import java.time.Instant
import java.util.UUID
import scala.language.implicitConversions
import scala.util.Failure

trait Column[T] {
  def apply[A](transformer: (Any, MetaDataItem) => Either[SqlRequestError, A]): Column[A] = new Column[A] {}
  def map[A](f: T => A): Column[A] = new Column[A] {}
}

object Column {
  def nonNull[A](transformer: (Any, MetaDataItem) => Either[SqlRequestError, A]): Column[A] = new Column[A] {}
  implicit def columnToArray[A](implicit column: Column[A], t: scala.reflect.ClassTag[A]): Column[Array[A]] =
    new Column[Array[A]] {}

  implicit val columnToString: Column[String] = new Column[String] {}

  implicit def columnToOption[T](implicit transformer: Column[T]): Column[Option[T]] = new Column[Option[T]] {}

  implicit val columnToInt: Column[Int] = new Column[Int] {}

  implicit val columnToInstant: Column[Instant] = new Column[Instant] {}

  implicit val columnToUUID: Column[UUID] = new Column[UUID] {}
}

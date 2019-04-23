package demo.support

import java.io.Serializable

import demo.{TsEventBase, TsEventWithLambda}

import scala.util.Try

trait Timestamped[T] extends Serializable {
  def timestampOf(obj: T): Long
}

object Timestamped {

  def apply[T](implicit ts: Timestamped[T]): Timestamped[T] = ts

  implicit def TryTimestamp[A : Timestamped]: Timestamped[Try[A]] = new Timestamped[Try[A]] {

    override def timestampOf(obj: Try[A]): Long =
      obj.toOption.map(Timestamped[A].timestampOf).getOrElse(Long.MinValue)

  }

  implicit def tsEventTimestamp[A <: TsEventBase]: Timestamped[A] = new Timestamped[A] {
    override def timestampOf(obj: A): Long = obj.ts
  }

  implicit val tsEventWithLambdaTimestamp: Timestamped[TsEventWithLambda] = new Timestamped[TsEventWithLambda] {
    override def timestampOf(obj: TsEventWithLambda): Long = obj.ts
  }
}
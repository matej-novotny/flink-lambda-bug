package demo

case class LambdaCaseClass(lambda: Long => Long = (in: Long) => 2 * in)

trait LambdaTrait {
  val lambda: LambdaCaseClass = LambdaCaseClass()
}

trait TsEventBase extends LambdaTrait {
  def ts: Long
}

class TsEventClass(val ts: Long) extends TsEventBase

case class TsEventCaseClass(override val ts: Long) extends TsEventBase

case class TsEventWithLambda(ts: Long, lambda: Long => Long = (in: Long) => 2 * in)

case class TsEventPlain(ts: Long)
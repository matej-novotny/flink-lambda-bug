package demo.support

import demo.{TsEventCaseClass, TsEventClass, TsEventWithLambda}

trait DecodedFromString[T] extends Serializable {
  def decodeFromString(str: String): T
}

object DecodedFromString {

  def apply[T](implicit decoded: DecodedFromString[T]): DecodedFromString[T] = decoded

  implicit val tsEventClassDecoder: DecodedFromString[TsEventClass] = new DecodedFromString[TsEventClass] {
    override def decodeFromString(str: String): TsEventClass = new TsEventClass(str.toLong)
  }

  implicit val tsEventCaseClassDecoder: DecodedFromString[TsEventCaseClass] = new DecodedFromString[TsEventCaseClass] {
    override def decodeFromString(str: String): TsEventCaseClass = TsEventCaseClass(str.toLong)
  }

  implicit val tsEventWithLambdaDecoder: DecodedFromString[TsEventWithLambda] = new DecodedFromString[TsEventWithLambda] {
    override def decodeFromString(str: String): TsEventWithLambda = TsEventWithLambda(str.toLong)
  }
}

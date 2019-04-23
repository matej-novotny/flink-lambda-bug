package demo

import java.nio.charset.StandardCharsets.UTF_8

import demo.support.DecodedFromString
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation

import scala.util.Try

class Deserializer[A : TypeInformation : DecodedFromString]
  extends AbstractDeserializationSchema[Try[A]] {

  override def isEndOfStream(nextElement: Try[A]): Boolean = false

  override def deserialize(message: Array[Byte]): Try[A] = {
    val messageStr = new String(message, UTF_8)

    Try(DecodedFromString[A].decodeFromString(messageStr))
  }
}
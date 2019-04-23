package demo

import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

import demo.support.{DecodedFromString, Timestamped}
import demo.support.Timestamped._
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010

import scala.collection.JavaConverters._
import scala.util.Try

object Sources {


  def kafkaSource
  [A : TypeInformation : DecodedFromString : Timestamped]
  (kafkaServer: String, groupId: String, topics: Seq[String]):
  SourceFunction[Try[A]] = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", kafkaServer)
    properties.setProperty("group.id", groupId)
    properties.setProperty("auto.offset.reset", "earliest")

    val consumer = new FlinkKafkaConsumer010(topics.asJava, new Deserializer[A], properties)
    consumer.assignTimestampsAndWatermarks(
      new WatermarkEmitter[Try[A]]
    )
  }

  def stalledEmptySource[T](maybeWatermark: Option[Long] = None): SourceFunction[T] = new SourceFunction[T] {
    private val isRunning = new AtomicBoolean(true)

    override def cancel(): Unit = isRunning.set(false)

    override def run(ctx: SourceFunction.SourceContext[T]): Unit = {
      maybeWatermark.foreach(w => ctx.emitWatermark(new Watermark(w)))
      while (isRunning.get) {
        Thread.sleep(1000)
      }
    }
  }
}

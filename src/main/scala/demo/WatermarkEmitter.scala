package demo

import demo.support.Timestamped
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

case class WatermarkEmitter[A : Timestamped]() extends AssignerWithPunctuatedWatermarks[A] {

  override def checkAndGetNextWatermark(element: A, ts: Long): Watermark = new Watermark(ts)
  override def extractTimestamp(element: A, previousTs: Long): Long = Timestamped[A].timestampOf(element)

}

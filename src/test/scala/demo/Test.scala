package demo

import demo.support.DecodedFromString._
import demo.support.Timestamped._
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable
import scala.util.Try

class Test extends FlatSpec  with Matchers with TestFlinkCluster with EmbeddedKafka {

  override def beforeEach(): Unit = {
    super.beforeEach()
    Test.timestamps.clear()
  }

  "Flink" should "process classes or fail compilation" in {
    val StopCondition: Long = 20
    withRunningKafkaOnFoundPort(embeddedKafkaConfig()) { kc =>
      val groupId = "test-group-id"
      val topic = "test-topic"
      val server = s"localhost:${kc.kafkaPort}"

      val source = env.
        addSource(
          Sources.kafkaSource[TsEventPlain](server, groupId, Seq(topic))
        ).
        flatMap(_.toOption).
        map { event =>
          Test.timestamps += event.ts

          event
        }.
        map { event =>
          if (event.ts == StopCondition) throw new IllegalArgumentException("stopping test")

          event
        }

      source.addSink(new PrintSinkFunction[TsEventPlain])

      publishStringMessageToKafka(topic, "0")(kc)
      publishStringMessageToKafka(topic, "1")(kc)
      publishStringMessageToKafka(topic, "2")(kc)
      publishStringMessageToKafka(topic, "invalid message")(kc)
      publishStringMessageToKafka(topic, "3")(kc)
      publishStringMessageToKafka(topic, StopCondition.toString)(kc)

      Try(env.execute())

      Test.timestamps.toList should be(List(0, 1, 2, 3, StopCondition))
    }
  }

  it should "process classes with hidden lambdas or fail compilation" in {
    val StopCondition: Long = 20
    withRunningKafkaOnFoundPort(embeddedKafkaConfig()) { kc =>
      val groupId = "test-group-id"
      val topic = "test-topic"
      val server = s"localhost:${kc.kafkaPort}"

      val source = env.
        addSource(
          Sources.kafkaSource[TsEventClass](server, groupId, Seq(topic))
        ).
        flatMap(_.toOption).
        map { event =>
          Test.timestamps += event.ts

          event
        }.
        map { event =>
          if (event.ts == StopCondition) throw new IllegalArgumentException("stopping test")

          event
        }

      source.addSink(new PrintSinkFunction[TsEventClass])

      publishStringMessageToKafka(topic, "0")(kc)
      publishStringMessageToKafka(topic, "1")(kc)
      publishStringMessageToKafka(topic, "2")(kc)
      publishStringMessageToKafka(topic, "invalid message")(kc)
      publishStringMessageToKafka(topic, "3")(kc)
      publishStringMessageToKafka(topic, StopCondition.toString)(kc)

      Try(env.execute())

      Test.timestamps.toList should be(List(0, 1, 2, 3, StopCondition))
    }
  }

  it should "process case classes with hidden lambdas or fail compilation" in {
    val StopCondition: Long = 20
    withRunningKafkaOnFoundPort(embeddedKafkaConfig()) { kc =>
      val groupId = "test-group-id"
      val topic = "test-topic"
      val server = s"localhost:${kc.kafkaPort}"

      val source = env.
        addSource(
          Sources.kafkaSource[TsEventCaseClass](server, groupId, Seq(topic))
        ).
        flatMap(_.toOption).
        map { event =>
          Test.timestamps += event.ts

          event
        }.
        map { event =>
          if (event.ts == StopCondition) throw new IllegalArgumentException("stopping test")

          event
        }

      source.addSink(new PrintSinkFunction[TsEventCaseClass])

      publishStringMessageToKafka(topic, "0")(kc)
      publishStringMessageToKafka(topic, "1")(kc)
      publishStringMessageToKafka(topic, "2")(kc)
      publishStringMessageToKafka(topic, "invalid message")(kc)
      publishStringMessageToKafka(topic, "3")(kc)
      publishStringMessageToKafka(topic, StopCondition.toString)(kc)

      Try(env.execute())

      Test.timestamps.toList should be(List(0, 1, 2, 3, StopCondition))
    }
  }

  it should "process case classes with lambdas or fail compilation" in {
    val StopCondition: Long = 20
    withRunningKafkaOnFoundPort(embeddedKafkaConfig()) { kc =>
      val groupId = "test-group-id"
      val topic = "test-topic"
      val server = s"localhost:${kc.kafkaPort}"

      val source = env.
        addSource(
          Sources.kafkaSource[TsEventWithLambda](server, groupId, Seq(topic))
        ).
        flatMap(_.toOption).
        map { event =>
          Test.timestamps += event.ts

          event
        }.
        map { event =>
          if (event.ts == StopCondition) throw new IllegalArgumentException("stopping test")

          event
        }

      source.addSink(new PrintSinkFunction[TsEventWithLambda])

      publishStringMessageToKafka(topic, "0")(kc)
      publishStringMessageToKafka(topic, "1")(kc)
      publishStringMessageToKafka(topic, "2")(kc)
      publishStringMessageToKafka(topic, "invalid message")(kc)
      publishStringMessageToKafka(topic, "3")(kc)
      publishStringMessageToKafka(topic, StopCondition.toString)(kc)

      Try(env.execute())

      Test.timestamps.toList should be(List(0, 1, 2, 3, StopCondition))
    }
  }

  private def embeddedKafkaConfig() = EmbeddedKafkaConfig(kafkaPort = 0, zooKeeperPort = 0)

}

object Test {

  val timestamps: mutable.Buffer[Long] = mutable.Buffer[Long]()
}
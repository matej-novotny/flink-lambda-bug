package demo

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.configuration.{ConfigConstants, Configuration, TaskManagerOptions}
import org.apache.flink.runtime.minicluster.LocalFlinkMiniCluster
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.util.TestStreamEnvironment
import org.apache.flink.test.util.TestEnvironment
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

import scala.collection.JavaConverters._

trait TestFlinkCluster extends BeforeAndAfterEach with BeforeAndAfterAll { this: Suite =>

  import TestFlinkCluster._

  val flinkParallelism: Int = Runtime.getRuntime.availableProcessors
  var cluster: LocalFlinkMiniCluster = _
  var env: StreamExecutionEnvironment = _

  protected def flinkEnvironment: StreamExecutionEnvironment = env

  override def beforeAll(): Unit = {
    try {
      super.beforeAll()
    } finally {
      val config = new Configuration
      config.setInteger(ConfigConstants.LOCAL_NUMBER_TASK_MANAGER, 1)
      config.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, flinkParallelism)
      config.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, false)

      config.setString(TaskManagerOptions.NETWORK_BUFFERS_MEMORY_MIN, (128 * MiB).toString)
      config.setString(TaskManagerOptions.NETWORK_BUFFERS_MEMORY_MAX, (128 * MiB).toString)
      config.setString(TaskManagerOptions.MANAGED_MEMORY_SIZE, (2 * MiB).toString)

      cluster = new LocalFlinkMiniCluster(config, SingleActorSystem)
      cluster.start()
    }
  }

  override def afterAll(): Unit = {
    try {
      super.beforeAll()
    } finally {
      cluster.stop()
    }
  }

  override def beforeEach(): Unit = {
    try {
      super.afterEach()
    } finally {
      TestStreamEnvironment.setAsContext(cluster, flinkParallelism)
      val testEnv = StreamExecutionEnvironment.getExecutionEnvironment
      val params: ParameterTool = ParameterTool.fromMap(Map[String, String]().asJava)
      testEnv.getConfig.setGlobalJobParameters(params)
      testEnv.getConfig.disableSysoutLogging()
      testEnv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
      testEnv.setParallelism(flinkParallelism)
      env = testEnv
    }
  }

  override def afterEach(): Unit = {
    try {
      super.afterEach()
    } finally {
      TestEnvironment.unsetAsContext()
    }
  }

}

object TestFlinkCluster {
  val MiB = 1024 * 1024
  val SingleActorSystem = true
}

name := "flink-lambda-bug"

version := "0.1"

//scalaVersion := "2.11.12"
scalaVersion := "2.12.8"

val flinkVersion = "1.7.2"
val scalaTestKafkaEmbeddedVersion = "0.14.0"
val catsVersion = "1.5.0"

libraryDependencies ++= Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" % "flink-metrics-core" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-metrics-jmx" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-test-utils" % flinkVersion % "test" exclude("com.google.guava", "guava"),
  "org.apache.flink" %% "flink-connector-kafka-0.10" % flinkVersion,
  "org.apache.flink" %% "flink-metrics-prometheus" % flinkVersion % "provided",
  "net.manub" %% "scalatest-embedded-kafka-streams" % scalaTestKafkaEmbeddedVersion % Test,
  "org.typelevel" %% "cats-core" % catsVersion
)
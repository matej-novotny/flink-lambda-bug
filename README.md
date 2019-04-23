# Demonstration of bug in Flink 1.7.2 with lambdas in Scala 2.12

This repo exists to demonstrate bug what I have found in Flink 1.7.2 on Scala 2.12.
When you use lambdas inside classes used in streams it does work in Scala 2.11.
It stoped working in Scala 2.12. It does compile but does not process any data and
does not throw any exception.
I don't think that Flink should support lambdas inside data classes 
processed in streams but I think that It should not behave like this.
I would expect that it will not compile in case I have used some not supported field in data class.

For more details check the code, please. The best place to start is `src/test/scala/demo/Test.scala`.

### Working example in Scala 2.11
 ```shell
git checkout 2.11
sbt test
 ```
 What results in:
 ```
 ...
[info] Test:
[info] Flink
[info] - should process classes with hidden lambdas or fail compilation
[info] - should process case classes with hidden lambdas or fail compilation
[info] - should process case classes with lambdas or fail compilation
[info] Run completed in 12 seconds, 372 milliseconds.
[info] Total number of tests run: 3
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 3, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
 ```
 
### Broken example in Scala 2.12
  ```shell
git checkout 2.12
sbt test
 ```
  What results in:
 ```
 ...
[info] Test:
[info] Flink
[info] - should process classes with hidden lambdas or fail compilation *** FAILED ***
[info]   List() was not equal to List(0, 1, 2, 3, 20) (Test.scala:54)
[info] - should process case classes with hidden lambdas or fail compilation
[info] - should process case classes with lambdas or fail compilation *** FAILED ***
[info]   List() was not equal to List(0, 1, 2, 3, 20) (Test.scala:130)
[info] Run completed in 13 seconds, 451 milliseconds.
[info] Total number of tests run: 3
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 1, failed 2, canceled 0, ignored 0, pending 0
[info] *** 2 TESTS FAILED ***
 ```
 Only difference is Scala version in `build.sbt`.
import io.gatling.sbt.GatlingPlugin

name := "stratio-datavis-system-test"

version := "1.0"

val test = project.in(file("."))
  .enablePlugins(GatlingPlugin)

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.0-SNAPSHOT" % "test"
//  ,"io.gatling" % "test-framework" % "1.0-RC5" % "test"
)

fork := false

logBuffered in Test := false

parallelExecution in Test := false

javaOptions in Test += "-Xdebug"

//javaOptions in Test += "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5009"
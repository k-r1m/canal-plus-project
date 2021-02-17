name := "GiveMeMovies"

version := "0.1"

libraryDependencies += "com.newmotion" %% "akka-rabbitmq" % "6.0.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.12",
  "com.typesafe.akka" %% "akka-stream" % "2.6.12",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.2",
  "org.apache.commons" % "commons-csv" % "1.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.6.12" % Test
)

scalaVersion := "2.13.4"

mainClass := Some("main.scala.GiveMeMovies")
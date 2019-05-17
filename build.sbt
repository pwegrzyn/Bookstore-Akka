name := "BookstoreSystem"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.22",
  "com.typesafe.akka" %% "akka-remote" % "2.5.22",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.22" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.22",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.22" % Test,
  "com.github.tototoshi" %% "scala-csv" % "1.3.5"
)
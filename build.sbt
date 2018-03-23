name := "elasticApp"

version := "0.1"

scalaVersion := "2.12.5"

val elastic4sVersion = "5.4.15"

libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-streams" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test",
  "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sVersion % "test"
)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.2.0"
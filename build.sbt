name := """happening"""
organization := "computer.hollis"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  guice,
  // Slick database access library and evolution support
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  // Postgres JDBC library
  "org.postgresql" % "postgresql" % "42.2.16",
  // SQLite JDBC library
  "org.xerial" % "sqlite-jdbc" % "3.36.0.1",
  // Scalatest Play Extensions
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "computer.hollis.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "computer.hollis.binders._"

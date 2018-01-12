lazy val commonSettings = Seq(
  organization := "com.sandinh",
  version := "1.0.0",
  scalaVersion := "2.12.4",
  scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-target:jvm-1.8")
)

def akkaHttp(m: String = "") = "com.typesafe.akka" %% s"akka-http$m"   % "10.1.0-RC1"

lazy val depsSettings = libraryDependencies ++= Seq(
  akkaHttp(), akkaHttp("-spray-json"),
  "com.typesafe.akka" %% "akka-stream" % "2.5.9"
)

lazy val dockerSettings = Seq(
  Docker / mappings := (Docker / mappings).value.filterNot(_._2.endsWith(".bat")),
  dockerUsername := Some("sandinh"),
  dockerBaseImage := "openjdk:8-alpine",
  dockerExposedPorts := Seq(8072)
)

lazy val ohzebot = project.in(file("."))
  .enablePlugins(DockerPlugin, AshScriptPlugin)
  .settings(commonSettings ++ depsSettings ++ dockerSettings: _*)

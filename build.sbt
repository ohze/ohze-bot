import com.typesafe.sbt.packager.docker.Cmd

lazy val commonSettings = Seq(
  organization := "com.sandinh",
  git.useGitDescribe := true,
  scalaVersion := "2.12.4",
  scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-target:jvm-1.8")
)

def akkaHttp(m: String = "") = "com.typesafe.akka" %% s"akka-http$m"   % "10.1.0-RC1"

lazy val depsSettings = libraryDependencies ++= Seq(
  akkaHttp(), akkaHttp("-spray-json"),
  "com.typesafe.akka" %% "akka-stream" % "2.5.9"
)

lazy val dockerSettings = Seq(
  Docker / mappings := (Docker / mappings).value
    .filterNot(_._2.endsWith(".bat"))
    .map {
      case (f, p) if p.startsWith(s"/opt/docker/lib/${organization.value}") =>
        f -> p.replace("/opt/", "/opt2/")
      case x => x
    },
  dockerUsername := Some("sandinh"),
  dockerBaseImage := "openjdk:8-alpine",
  dockerExposedPorts := Seq(8072),
  dockerCommands := {
    val (l, r) = dockerCommands.value.span {
      case Cmd("ADD", args) => !args.startsWith("--chown=")
      case _ => true
    }
    (l :+
      r.head :+
      Cmd("ADD", "--chown=daemon:daemon", "opt2", "/opt")
    ) ++ r.tail
  }
)

lazy val ohzebot = project.in(file("."))
  .enablePlugins(DockerPlugin, AshScriptPlugin, GitVersioning)
  .settings(commonSettings ++ depsSettings ++ dockerSettings: _*)

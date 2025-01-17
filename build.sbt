import com.typesafe.config.ConfigFactory
import sbt.Keys._

val appConf = ConfigFactory.parseFile(new File("src/main/resources/reference.conf")).resolve().getConfig("app")

organization in ThisBuild := "com.wavesplatform"
name := "df"
version := appConf.getString("version")
scalaVersion := "2.12.1"
libraryDependencies ++= Seq( "org.scala-lang" % "scala-swing" % "2.10+",
 "com.github.scopt" %% "scopt" % "3.5.0",
 "com.h2database" % "h2-mvstore" % "1.4.196",
 "ch.qos.logback" % "logback-classic" % "1.1.9",
 "com.github.swagger-akka-http" % "swagger-akka-http_2.12" % "0.9.1",
 "com.typesafe.akka" % "akka-actor_2.12" % "2.5.23",
 "com.typesafe.akka" % "akka-http_2.12" % "10.1.4",
 "com.typesafe.akka" % "akka-http-core_2.12" % "10.1.4",
 "com.typesafe.akka" % "akka-slf4j_2.12" % "2.5.23",
 "com.typesafe.akka" % "akka-stream_2.12" % "2.5.23",
 "com.typesafe.play" % "play-json_2.12" % "2.6.0-M4",
 "com.typesafe" % "config" % "1.3.1",
 "io.swagger" % "swagger-annotations" % "1.5.12",
 "io.swagger" % "swagger-jaxrs" % "1.5.12",
 "io.swagger" % "swagger-models" % "1.5.12",
 "org.scalaj" % "scalaj-http_2.12" % "2.3.0",
 "org.slf4j" % "slf4j-api" % "1.7.24",
 "com.iheart" % "ficus_2.12" % "1.4.0",
 "org.scorexfoundation" %% "scrypto" % "1.2.0",
 "fr.davit" %% "akka-http-metrics-prometheus" % "0.6.0"


)
enablePlugins(DockerPlugin)
assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

dockerfile in docker := {
 val artifact: File = assembly.value
 val artifactTargetPath = s"/app/${artifact.name}"

 new Dockerfile {
  from("openjdk:8-jre")
  add(artifact, artifactTargetPath)
  add(baseDirectory(_ / "wdf.conf").value, file("/wdf.conf"))
  expose(6990)
  entryPoint("java", "-jar", artifactTargetPath, "/wdf.conf")
 }
}
buildOptions in docker := BuildOptions(
 cache = false,
 removeIntermediateContainers = BuildOptions.Remove.Always,
 pullBaseImage = BuildOptions.Pull.Always
)
// Set names for the image
imageNames in docker := Seq(
 ImageName(s"docker.decentralchain.io/${name.value}:${version.value}")
)
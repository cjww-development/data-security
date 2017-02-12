import com.typesafe.config.ConfigFactory
import scala.util.{Failure, Success, Try}

val bTVersion : String = {
  Try(ConfigFactory.load.getString("version")) match {
    case Success(ver) => ver
    case Failure(_) => "INVALID_RELEASE_VERSION"
  }
}

name := "data-security"
version := bTVersion
scalaVersion := "2.11.8"
organization := "com.cjww-dev.libs"

libraryDependencies ++= Seq(
  "commons-codec" % "commons-codec" % "1.10",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.play" % "play_2.11" % "2.5.12",
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "1.5.1"
)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

bintrayOrganization := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := false
bintrayRepository := "releases"
bintrayOmitLicense := true

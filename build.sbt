import com.typesafe.config.ConfigFactory
import scala.util.{Failure, Success, Try}

val bTVersion : String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

name := "data-security"
version := bTVersion
scalaVersion := "2.11.11"
organization := "com.cjww-dev.libs"

libraryDependencies ++= Seq(
  "commons-codec" % "commons-codec" % "1.10",
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "3.1.0"
)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

bintrayOrganization := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := true
bintrayRepository := "releases"
bintrayOmitLicense := true

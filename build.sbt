import com.typesafe.config.ConfigFactory
import scala.util.{Failure, Success, Try}

val libraryName = "data-security"

val bTVersion : String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val dependencies: Seq[ModuleID] = Seq(
  "commons-codec"          % "commons-codec"           % "1.11",
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "2.0.1"
)

lazy val library = Project(libraryName, file("."))
  .settings(
    version                              :=  bTVersion,
    scalaVersion                         :=  "2.11.12",
    organization                         :=  "com.cjww-dev.libs",
    resolvers                            +=  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    libraryDependencies                  ++= dependencies,
    bintrayOrganization                  :=  Some("cjww-development"),
    bintrayReleaseOnPublish in ThisBuild :=  true,
    bintrayRepository                    :=  "releases",
    bintrayOmitLicense                   :=  true
  )

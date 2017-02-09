name := "data-security"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "commons-codec" % "commons-codec" % "1.10",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.play" % "play_2.11" % "2.5.12",
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "1.5.1"
)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

bintrayOrganization := Some("cjww-development")
name := "AdvRecomm"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.2"

libraryDependencies += "com.hankcs" % "hanlp" % "portable-1.2.4"

libraryDependencies += "de.l3s.boilerpipe" % "boilerpipe" % "1.1.0"

libraryDependencies += "xerces" % "xercesImpl" % "2.11.0"

libraryDependencies += "net.sourceforge.nekohtml" % "nekohtml" % "1.9.13"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"


libraryDependencies  ++= Seq(
  // other dependencies here
  "org.scalanlp" %% "breeze" % "0.10",
  // native libraries are not included by default. add this if you want them (as of 0.7)
  // native libraries greatly improve performance, but increase jar sizes.
  "org.scalanlp" %% "breeze-natives" % "0.10"
)

resolvers ++= Seq(
  // other resolvers here
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)
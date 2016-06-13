name := "hello-slick"

mainClass in Compile := Some("HelloSlick")

scalaVersion := "2.11.6"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.175",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

resolvers += "johnreed2 bintray" at "http://dl.bintray.com/content/johnreed2/maven"

libraryDependencies += "scala.trace" %% "scala-trace-debug" % "2.2.17"

scalacOptions ++= Seq("-Xfatal-warnings", "-deprecation", "-print", "-unchecked", "-feature", "-Xlint", "-Yinline-warnings", "-Ywarn-inaccessible", "-Ywarn-nullary-override", "-Ywarn-nullary-unit")

/*
[warn] Multiple dependencies with the same organization/name but different versions.
To avoid conflict, pick one version:
[warn] * org.scala-lang.modules:scala-xml_2.11:(1.0.4, 1.0.3)
 */

libraryDependencies += "org.scala-lang.modules"  %% "scala-xml"      % "1.0.4"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

/*

libraryDependencies += "org.scala-lang.modules"  % "scala-xml_2.11"      % "1.0.4"


So this was a "feature" that I introduced in 0.13.8 in response to #1634. Using the repro steps set up by Dale, here's what you see when you run show updateClassifiers:

[warn] Multiple dependencies with the same organization/name but different versions. To avoid conflict, pick one version:
[warn]  * org.scala-lang:scala-library:(2.11.6, 2.11.4)
[info]  compile:
[info]  org.scala-lang:scala-library
[info]      - 2.11.6
[info]          (Artifact(scala-library,doc,jar,Some(javadoc),ArraySeq(),None,Map()),/Users/eugene/.ivy2/cache/org.scala-lang/scala-library/docs/scala-library-2.11.6-javadoc.jar)
[info]          (Artifact(scala-library,src,jar,Some(sources),ArraySeq(),None,Map()),/Users/eugene/.ivy2/cache/org.scala-lang/scala-library/srcs/scala-library-2.11.6-sources.jar)
[info]          (Artifact(scala-library,jar,jar,None,ArraySeq(),None,Map()),/Users/eugene/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.6.jar)
[info]          status: release
[info]          publicationDate: Wed Feb 25 20:01:05 EST 2015
[info]          resolver: sbt-chain
[info]          artifactResolver: sbt-chain
[info]          evicted: false
...
[info]  provided:
...
[info]  org.scala-lang:scala-library
[info]      - 2.11.4
[info]          (Artifact(scala-library,src,jar,Some(sources),ArraySeq(),None,Map()),/Users/eugene/.ivy2/cache/org.scala-lang/scala-library/srcs/scala-library-2.11.4-sources.jar)
[info]          (Artifact(scala-library,doc,jar,Some(javadoc),ArraySeq(),None,Map()),/Users/eugene/.ivy2/cache/org.scala-lang/scala-library/docs/scala-library-2.11.4-javadoc.jar)
[info]          status: release
[info]          publicationDate: Thu Oct 23 11:34:51 EDT 2014
[info]          resolver: sbt-chain
[info]          artifactResolver: sbt-chain
[info]          evicted: false
This shows that both Compile configuration and Provided configuration are pulling in scala-library, but with different versions. inconsistentDuplicateWarning method (https://github.com/sbt/sbt/blob/0.13.9/ivy/src/main/scala/sbt/Ivy.scala#L534-L555) either needs to be smarter about configurations or needs to be commented out.

 */

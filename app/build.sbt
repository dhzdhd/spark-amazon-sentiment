val sparkVersion = settingKey[String]("Spark version")

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "dev.dhzdhd",
      scalaVersion := "2.12.15"
    )
  ),
  name := "amazonSentiment",
  version := "0.0.1",
  sparkVersion := "3.4.0",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javaOptions ++= Seq("-Xms512M", "-Xmx2048M"),
  scalacOptions ++= Seq("-deprecation", "-unchecked"),
  parallelExecution in Test := false,
  fork := true,
  coverageHighlighting := true,
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-streaming" % "3.4.0" % "provided",
    "org.apache.spark" %% "spark-sql" % "3.4.0" % "provided",
    "org.apache.spark" %% "spark-mllib" % "3.4.0" % "provided",
    // "org.scalatest" %% "scalatest" % "3.2.2" % "test",
    // "org.scalacheck" %% "scalacheck" % "1.15.2" % "test",
    "com.johnsnowlabs.nlp" %% "spark-nlp" % "5.3.1"
    // "com.holdenkarau" %% "spark-testing-base" % "$sparkVersion$_$sparkTestingbaseRelease$" % "test"
  ),
  // uses compile classpath for the run task, including "provided" jar (cf http://stackoverflow.com/a/21803413/3827)
  run in Compile := Defaults
    .runTask(
      fullClasspath in Compile,
      mainClass in (Compile, run),
      runner in (Compile, run)
    )
    .evaluated,
  scalacOptions ++= Seq("-deprecation", "-unchecked"),
  pomIncludeRepository := { x => false },
  // resolvers ++= Seq(
  //   "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  //   "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  //   "Second Typesafe repo" at "https://repo.typesafe.com/typesafe/maven-releases/",
  //   "Maven Central" at "https://repo1.maven.org/maven2/",
  //   Resolver.sonatypeRepo("public")
  // ),
  pomIncludeRepository := { _ => false },

  // publish settings
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

assembly / test := {}

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*)      => MergeStrategy.discard
  case x if x.startsWith("NativeLibrary") => MergeStrategy.last
  case x if x.startsWith("aws")           => MergeStrategy.last
  case _                                  => MergeStrategy.last
}

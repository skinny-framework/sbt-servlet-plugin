import sbt._
import sbt.Keys._

lazy val jettyVersion = "9.3.16.v20170120"
lazy val root = Project(id = "sbt-servlet-plugin", base = file("."),
  settings = Seq(
    organization := "org.skinny-framework",
    name := "sbt-servlet-plugin",
    version := "2.1.2",
    sbtPlugin := true,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    incOptions := sbt.inc.IncOptions.Default,
    transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
    libraryDependencies ++= Seq(
      "org.eclipse.jetty" % "jetty-servlet"     % jettyVersion,
      "org.eclipse.jetty" % "jetty-server"      % jettyVersion,
      "org.eclipse.jetty" % "jetty-webapp"      % jettyVersion,
      "org.eclipse.jetty" % "jetty-annotations" % jettyVersion,
      "org.eclipse.jetty" % "jetty-plus"        % jettyVersion
    ),
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>https://github.com/skinny-framework/sbt-servlet-plugin</url>
      <licenses>
        <license>
          <name>BSD 3-Clause</name>
          <url>https://github.com/skinny-framework/sbt-servlet-plugin/blob/master/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:skinny-framework/sbt-servlet-plugin.git</url>
        <connection>scm:git:git@github.com:skinny-framework/sbt-servlet-plugin.git</connection>
      </scm>
      <developers>
        <developer>
          <id>siasia</id>
          <name>Artyom Olshevskiy</name>
          <url>http://github.com/siasia</url>
        </developer>
        <developer>
          <id>JamesEarlDouglas</id>
          <name>James Earl Douglas</name>
          <url>https://github.com/JamesEarlDouglas</url>
        </developer>
        <developer>
          <id>cdow</id>
          <name>cdow</name>
          <url>https://github.com/cdow</url>
        </developer>
        <developer>
          <id>seratch</id>
          <name>Kazuhiro Sera</name>
          <url>https://github.com/seratch</url>
        </developer>
      </developers>)
  )
).settings(scalariformSettings)

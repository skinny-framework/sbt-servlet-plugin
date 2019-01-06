## sbt-servlet-plugin

This is a sbt plugin which is based on xsbt-web-plugin 0.9.1. The reason why we forked the sbt plugin is that the version allowed us to reload Scalate templates without restarting a Servlet container every time a user changes them a bit.

[![Build Status](https://travis-ci.org/skinny-framework/sbt-servlet-plugin.svg?branch=master)](https://travis-ci.org/skinny-framework/sbt-servlet-plugin)

## Getting started 

### project/plugins.sbt

Add this sbt plugin to your `project/plugins.sbt`.

```scala
addSbtPlugin("org.skinny-framework" % "sbt-servlet-plugin" % "3.0.6")
```

### build.sbt

Add Jetty dependencies into "container" scope.

```scala
import skinny.servlet._, ServletPlugin._, ServletKeys._

lazy val jettyVersion = "9.4.12.v20180830"

lazy val root = (project in file("."))
  .settings(ServletPlugin.projectSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.eclipse.jetty" % "jetty-webapp"      % jettyVersion % "container",
      "org.eclipse.jetty" % "jetty-plus"        % jettyVersion % "container",
      "javax.servlet"     % "javax.servlet-api" % "3.1.0"      % "provided"
    )
  )
  .enablePlugins(ServletPlugin)
```

## Development

### Run tests for this sbt plugin

```
sbt scripted
```

### Publish new versions

https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html

```
sbt publishSigned sonatypeRelease
```

## License

the BSD 3-Clause license


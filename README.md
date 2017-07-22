## sbt-servlet-plugin

An sbt plugin to build Servlet applications which is based on xsbt-web-plugin 0.9.1.
xsbt-web-plugin 0.9 is very suitable for our use case.
The version implementation allows to reload Scalate templates without restarting Servlet containers and Servlet invocation is faster than 1.x.

[![Build Status](https://travis-ci.org/skinny-framework/sbt-servlet-plugin.svg?branch=2.1.x)](https://travis-ci.org/skinny-framework/sbt-servlet-plugin)

## Getting started 

### project/plugins.sbt

Add this sbt plugin to your `project/plugins.sbt`.

```scala
addSbtPlugin("org.skinny-framework" % "sbt-servlet-plugin" % "2.1.5")
```

### build.sbt

```scala
servletSettings
```

### project/Build.scala 

Add Jetty dependencies into "container" scope.

```scala
import skinny.servlet._, ServletPlugin._, ServletKeys._

lazy val jettyVersion = "9.3.20.v20170531"

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "container",
  "org.eclipse.jetty" % "jetty-plus"   % jettyVersion % "container"
)
```

## License

the BSD 3-Clause license


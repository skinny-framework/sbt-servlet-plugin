enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.11.v20180605" % "container"

fork in run := true

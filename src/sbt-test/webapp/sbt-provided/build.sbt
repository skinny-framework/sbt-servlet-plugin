enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.12.v20180830" % "container"

fork in run := true

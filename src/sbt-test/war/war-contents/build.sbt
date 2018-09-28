enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

name := "war-contents"
version := "1.2.3"

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.12.v20180830" % "container"

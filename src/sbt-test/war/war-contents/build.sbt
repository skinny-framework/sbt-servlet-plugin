enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

name := "war-contents"
version := "1.2.3"

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.19.v20190610" % "container"

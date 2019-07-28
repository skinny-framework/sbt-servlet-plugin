enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.19.v20190610" % {
  if ((new File("jetty-conf")).exists == false) "container"
  else "test"
}

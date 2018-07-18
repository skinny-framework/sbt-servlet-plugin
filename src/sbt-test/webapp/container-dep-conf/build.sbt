enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.11.v20180605" % {
  if ((new File("jetty-conf")).exists == false) "container"
  else "test"
}

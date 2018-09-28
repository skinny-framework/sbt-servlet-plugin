enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.12.v20180830" % {
  if ((new File("jetty-conf")).exists == false) "container"
  else "test"
}

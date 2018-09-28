enablePlugins(ServletPlugin)

ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.12.v20180830" % "container"

// this test case expects "extras/resource.txt" is copied into target/webapp/WEB-INF/classes/
fullClasspath in Runtime in packageWar += baseDirectory.map(bd => bd / "extras").value

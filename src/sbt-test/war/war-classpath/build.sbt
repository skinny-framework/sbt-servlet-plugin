enablePlugins(ServletPlugin)

ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.11.v20180605" % "container"

// this test case expects "extras/resource.txt" is copied into target/webapp/WEB-INF/classes/
fullClasspath in Runtime in packageWar += baseDirectory.map(bd => bd / "extras").value

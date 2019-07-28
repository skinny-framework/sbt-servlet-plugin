enablePlugins(ServletPlugin)

ServletPlugin.projectSettings

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.19.v20190610" % "container"

// this test case expects "extras/resource.txt" is copied into target/webapp/WEB-INF/classes/
fullClasspath in Runtime in packageWar += baseDirectory.map(bd => bd / "extras").value

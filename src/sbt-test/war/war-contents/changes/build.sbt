enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

name := "war-contents"
version := "1.2.3"

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.11.v20180605" % "container"

// ------------ DIFF ---------------
fullClasspath in Runtime in packageWar += baseDirectory.map(bd => bd / "extras").value
classesAsJar in Compile := true
// --------------------------------

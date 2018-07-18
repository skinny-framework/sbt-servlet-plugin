import sbt._
import sbt.internal.util.complete.Parsers

enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

def jettyPort = 7101
def indexURL = new java.net.URL("http://localhost:" + jettyPort)
def indexFile = new java.io.File("index.html")

lazy val getPage = taskKey[Unit]("getPage")
lazy val checkPage = inputKey[Unit]("checkPage")

// NOTE: Jetty 9.4 doesn't provide jetty-jsp module
val jettyVersion = "9.2.25.v20180606"
libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "container",
  "org.eclipse.jetty" % "jetty-jsp"    % jettyVersion % "container",
  "org.eclipse.jetty" % "jetty-plus"   % jettyVersion % "container"
)

lazy val Conf = config("container")
port in Conf := jettyPort
scanInterval in Compile := 60

getPage := {
  val body = IO.readLinesURL(indexURL).mkString("\n")
  IO.write(indexFile, body)
}

checkPage := {
  val args = Parsers.spaceDelimited("<arg>").parsed
  val content: String = IO.read(indexFile)
  val checkString = args.mkString(" ")
  val verificationResult: Option[String] = {
    if (content.contains(checkString)) None
    else Some(s"index.html did not contain '${checkString}' :\n${content}")
  }
  verificationResult.foreach(sys.error)
}

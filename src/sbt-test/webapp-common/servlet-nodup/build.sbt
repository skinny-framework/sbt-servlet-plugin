import sbt._
import sbt.internal.util.complete.Parsers

enablePlugins(ServletPlugin)
ServletPlugin.projectSettings

def jettyPort = 7104
def indexURL = new java.net.URL("http://localhost:" + jettyPort)
def indexFile = new java.io.File("index.html")

lazy val getPage = taskKey[Unit]("getPage")
lazy val checkPage = inputKey[Unit]("checkPage")

val jettyVersion = "9.4.19.v20190610"
libraryDependencies ++= Seq(
  "org.eclipse.jetty"      %  "jetty-webapp"      % jettyVersion % "container",
  "org.eclipse.jetty"      %  "jetty-plus"        % jettyVersion % "container",
  "javax.servlet"          %  "javax.servlet-api" % "3.1.0"      % "provided",
  "org.scala-lang.modules" %% "scala-xml"         % "1.2.0"
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

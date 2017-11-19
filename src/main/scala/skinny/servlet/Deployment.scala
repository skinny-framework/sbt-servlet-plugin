package skinny.servlet

import sbt._

case class Deployment(
  webappResources: Seq[File],
  classpath: Seq[File],
  scanDirectories: Seq[File],
  scanInterval: Int,
  env: Option[File],
  webInfIncludeJarPattern: Option[String])


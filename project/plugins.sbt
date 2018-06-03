libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("org.scalariform"      % "sbt-scalariform"      % "1.8.2")
addSbtPlugin("com.jsuereth"         % "sbt-pgp"              % "1.0.0")
addSbtPlugin("com.timushev.sbt"     % "sbt-updates"          % "0.3.3")
addSbtPlugin("net.virtual-void"     % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("org.xerial.sbt"       % "sbt-sonatype"         % "1.1")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("org.scalariform"      % "sbt-scalariform"      % "1.6.0")
addSbtPlugin("com.github.mpeltonen" % "sbt-idea"             % "1.6.0")
addSbtPlugin("com.jsuereth"         % "sbt-pgp"              % "1.0.0")
addSbtPlugin("com.timushev.sbt"     % "sbt-updates"          % "0.3.0")
addSbtPlugin("net.virtual-void"     % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("org.xerial.sbt"       % "sbt-sonatype"         % "1.1")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

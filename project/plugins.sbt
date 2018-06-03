libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("org.scalariform"      % "sbt-scalariform"      % "1.8.2")
addSbtPlugin("com.github.mpeltonen" % "sbt-idea"             % "1.6.0")
addSbtPlugin("com.jsuereth"         % "sbt-pgp"              % "1.1.1")
addSbtPlugin("com.timushev.sbt"     % "sbt-updates"          % "0.3.4")
addSbtPlugin("net.virtual-void"     % "sbt-dependency-graph" % "0.9.0")
addSbtPlugin("org.xerial.sbt"       % "sbt-sonatype"         % "2.3")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

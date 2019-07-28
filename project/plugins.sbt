libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("org.scalariform"      % "sbt-scalariform"      % "1.8.3")
addSbtPlugin("com.jsuereth"         % "sbt-pgp"              % "1.1.2")
addSbtPlugin("com.timushev.sbt"     % "sbt-updates"          % "0.4.2")
addSbtPlugin("net.virtual-void"     % "sbt-dependency-graph" % "0.9.2")
addSbtPlugin("org.xerial.sbt"       % "sbt-sonatype"         % "2.5")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion == null) {
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Please specify this property using the SBT flag -D.""".stripMargin)
  } else {
    addSbtPlugin("org.skinny-framework" % "sbt-servlet-plugin" % pluginVersion)
  }
}

scriptedBufferLog := false
ScriptedPlugin.scriptedSettings
scriptedLaunchOpts += s"-Dplugin.version=${version.value}"

/* Replace the default scripted task so we can run certain tests once for each
 * supported servlet container.
 */
scripted := Def.inputTask {
  val deps = scriptedDependencies.value
  val m = scriptedTests.value
  val r = scriptedRun.value
  val testdir = sbtTestDirectory.value
  val bufferlog = scriptedBufferLog.value
  val version = scriptedSbt.value
  val launcher = sbtLauncher.value
  val launchOpts = scriptedLaunchOpts.value
  val args = complete.Parsers.spaceDelimited("<arg>").parsed
  // Due to an API change in SBT 0.13 we have to try 2 argument lists when trying to run scripted tests
  def runTests(tests: Array[String], options: Array[String]): Unit = {
    r.invoke(m, testdir, bufferlog: java.lang.Boolean, tests, launcher, options)
  }
  // If we don't get any test arguments we need to find all of the tests
  // so we can separate out the shared container tests.  Otherwise scripted
  // will just run all of the tests once.
  val tests: Seq[String] = if(args.isEmpty) {
    for(group <-testdir.listFiles;
        if group.isDirectory;
        test <- group.listFiles;
        if test.isDirectory) yield { group.name + "/" + test.name }
  } else {
    args
  }
  // Separate out the shared container tests
  val (containerTests, regularTests) = tests.partition(_.startsWith("webapp-common"))
  try {
    // Run the regular tests once
    if(!regularTests.isEmpty) {
      runTests(regularTests.toArray, launchOpts.toArray)
    }
    // run the shared container tests once for each supported container
    if(!containerTests.isEmpty) {
      val supportedContainers = Seq("jetty9")
      supportedContainers.foreach { container =>
        println("===== Shared container tests for " + container + " =====")
        val containerOpt = "-Dplugin.container=" + container
        // We have to pass the location of the test dir to the tests so
        // they can find shared sources.  This is only necessary when
        // running them through scripted since the scripted plugin runs
        // the tests in a temp dir.  When running the tests directly we
        // just use a relative path.
        val commonTestDirOpt = "-Dplugin.webapp.common.dir=" + testdir.getPath + "/"
        val opts = launchOpts.toArray :+ containerOpt :+ commonTestDirOpt
        runTests(containerTests.toArray, opts)
      }
    }
  } catch { case e: java.lang.reflect.InvocationTargetException => throw e.getCause }
}.evaluated

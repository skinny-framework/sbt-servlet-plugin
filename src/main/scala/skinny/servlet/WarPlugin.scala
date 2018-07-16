package skinny.servlet

import sbt.{ `package` => _, _ }
import Path.{ relativeTo, flat }
import Def.Initialize
import Defaults.packageTaskSettings
import Keys._
import ServletKeys._

object WarPlugin extends AutoPlugin {

  private def copyFlat(sources: Iterable[File], destinationDirectory: File): Set[File] = {
    IO.copy(sources.map { source =>
      (source.asFile, destinationDirectory / source.getName)
    })
  }

  def packageWarTask(classpathConfig: Configuration): Initialize[Task[Seq[(File, String)]]] = Def.task {
    val classpath = (fullClasspath in classpathConfig in packageWar).value.map(_.data)
    val warPath = target.value / "webapp"
    val log = streams.value.log.asInstanceOf[AbstractLogger]
    val webInfPath = warPath / "WEB-INF"
    val webLibDirectory = webInfPath / "lib"
    val classesTargetDirectory = webInfPath / "classes"
    val filter = excludeFilter.value

    val (libs, directories) = classpath.toList.filter(_.exists).partition(!_.isDirectory)
    val wcToCopy: Seq[(File, File)] = for {
      dir <- webappResources.value.reverse
      file <- (dir ** (-filter)).get
      target = Path.rebase(dir, warPath)(file).get
    } yield (file, target)

    if (log.atLevel(Level.Debug)) {
      directories foreach { d =>
        log.debug(" Copying the contents of directory " + d + " to " + classesTargetDirectory)
      }
    }

    val copiedWebapp = IO.copy(
      sources = wcToCopy,
      overwrite = true,
      preserveLastModified = true,
      preserveExecutable = false)
    val copiedLibs = copyFlat(libs, webLibDirectory)

    val toRemove = scala.collection.mutable.HashSet((warPath ** "*").get: _*)
    if (classesAsJar.value) {
      val classesAndResources = for {
        dir <- directories.reverse
        file <- (dir ** (-filter)).get
        target = Path.rebase(dir, "")(file).get
      } yield (file, target)
      val classesAndResourcesJar = webLibDirectory / (name.value + "-" + version.value + ".jar")
      IO.jar(classesAndResources, classesAndResourcesJar, new java.util.jar.Manifest)
      toRemove -= classesAndResourcesJar
    } else {
      val classesAndResources = for {
        dir <- directories.reverse
        file <- (dir ** (-filter)).get
        target = Path.rebase(dir, classesTargetDirectory)(file).get
      } yield (file, target)
      val copiedClasses = IO.copy(
        sources = classesAndResources,
        overwrite = true,
        preserveLastModified = true,
        preserveExecutable = false)
      toRemove --= copiedClasses
    }

    toRemove --= copiedWebapp
    toRemove --= copiedLibs

    val (dirs, files) = toRemove.toList.partition(_.isDirectory)
    if (log.atLevel(Level.Debug)) {
      files.foreach(r => log.debug("Pruning file " + r))
    }
    IO.delete(files)
    IO.deleteIfEmpty(dirs.toSet)
    warPostProcess.value(warPath)
    warPath ** (-filter) pair (relativeTo(warPath) | flat)
  }

  def warSettings0(classpathConfig: Configuration): Seq[Setting[_]] = {
    packageTaskSettings(packageWar, packageWarTask(classpathConfig)) ++
      Seq(
        webappResources := Seq(sourceDirectory.value / "webapp"),
        webappResources ++= webappResources.all(ScopeFilter(tasks = inTasks(compile))).apply(_.flatten).value,
        artifact in packageWar := Artifact(moduleName.value, "war", "war"),
        publishArtifact in packageBin := false,
        warPostProcess := { _ => () },
        classesAsJar := false,
        `package` := (packageWar dependsOn packageWebapp).value,
        packageWebapp := packageWarTask(classpathConfig).value)
  }

  private def warSettings0: Seq[Setting[_]] = warSettings0(DefaultClasspathConf)

  def globalWarSettings: Seq[Setting[_]] = {
    Seq(addArtifact(artifact in (DefaultConf, packageWar), packageWar in DefaultConf): _*)
  }

  private def warSettings(classpathConfig: Configuration): Seq[Setting[_]] = {
    inConfig(DefaultConf)(warSettings0(classpathConfig)) ++ globalWarSettings
  }

  private def warSettings: Seq[Setting[_]] = warSettings(DefaultClasspathConf)

  override val projectSettings = globalWarSettings
}

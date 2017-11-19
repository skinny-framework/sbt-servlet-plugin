package skinny.servlet

import sbt._
import Keys._
import ServletKeys._
import WarPlugin.warSettings0

object WebappPlugin extends Plugin {

  def auxCompileTask = Def.task {
    val _ = compile.value
    val cd = classDirectory.value
    val auxCd = crossTarget.value / "aux-classes"
    val classes = for {
      file <- cd.descendantsExcept("*", excludeFilter.value).get
      target = Path.rebase(cd, auxCd)(file).get
    } yield (file, target)
    val copied = IO.copy(classes)
    val toRemove = scala.collection.mutable.HashSet((auxCd ** "*").get.toSeq: _*) -- copied
    val (dirs, files) = toRemove.toList.partition(_.isDirectory)
    IO.delete(files)
    IO.deleteIfEmpty(dirs.toSet)
  }

  def webappSettings0(classpathConfig: Configuration): Seq[Setting[_]] = warSettings0(classpathConfig) ++ Seq(
    scanDirectories := Seq(crossTarget.value / "aux-classes"),
    auxCompile := auxCompileTask.value,
    scanInterval := 3,
    env := None,
    webInfIncludeJarPattern := None,
    deployment := {
      Deployment(webappResources.value, (fullClasspath in classpathConfig).value.map(_.data), scanDirectories.value, scanInterval.value, env.value, webInfIncludeJarPattern.value)
    })

  def webappSettings0: Seq[Setting[_]] = webappSettings0(DefaultClasspathConf)

  def webappSettings(cc: Configuration): Seq[Setting[_]] = inConfig(DefaultConf)(webappSettings0(cc)) ++ WarPlugin.globalWarSettings

  def webappSettings: Seq[Setting[_]] = webappSettings(DefaultClasspathConf)

}

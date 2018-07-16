package skinny.servlet

import scala.language.implicitConversions

import java.net.InetSocketAddress

import sbt.Classpaths.managedJars
import sbt.Keys._
import sbt.Scoped._
import sbt._
import sbt.complete._
import skinny.servlet.ServletKeys._

import scala.xml.NodeSeq

import sjsonnew.BasicJsonProtocol._

case class Container(name: String) {

  type SettingSeq = Seq[Setting[_]]

  lazy val Configuration = config(name).hide
  def attribute = AttributeKey[Runner](name)
  def runner = attribute

  private implicit def attributeToRunner[Runner](
    key: AttributeKey[Runner])(implicit state: State): Runner = {
    state.get(key).get
  }

  private implicit def stateToRunner(state: State): Runner = {
    state.get(attribute).get
  }

  object Impl {

    private implicit def keyToResult[T](key: TaskKey[T])(implicit state: State): T = eval(key)

    def eval[T](key: ScopedKey[sbt.Task[T]])(implicit state: State): T = {
      EvaluateTask.processResult(
        Project.runTask(key, state).map(_._2).getOrElse {
          sys.error("Error getting " + key)
        },
        state.log)
    }

    def newRunner(ref: ProjectRef, state: State) = {
      implicit val s = state
      val classpath = Attributed.data(fullClasspath in (ref, Configuration))
      state.put(attribute, Runner(classpath))
    }

  }

  def globalSettings: Seq[Def.Setting[Seq[Configuration]]] = Seq(
    ivyConfigurations += Configuration)

  def containerSettings = Seq(
    managedClasspath := managedJars(Configuration, classpathTypes.value, update.value),
    fullClasspath := managedClasspath.value,
    onLoad in Global := {
      val (load, containerProject) = ((onLoad in Global).value, thisProjectRef.value)
      (state) => Impl.newRunner(containerProject, load(state))
    },
    onUnload in Global := {
      val load = (onUnload in Global).value
      (state) => {
        state.stop()
        load(state)
      }
    },
    host := "0.0.0.0",
    port := 8080,
    ssl := None,
    launch := ((state) map { (state) => state.join() } dependsOn (start in Configuration)).value,
    start := {
      val (cc, cf, cx) = (customConfiguration.value, configurationFiles.value, configurationXml.value)
      state.value.start(
        addr = new InetSocketAddress(host.value, port.value),
        ssl = toSslSettings(ssl.value),
        logger = new FullLogger(state.value.log),
        apps = apps.value,
        customConf = cc,
        confFiles = cf,
        confXml = cx)
    },
    discoveredContexts := (apps map discoverContexts storeAs discoveredContexts triggeredBy start).value,
    reload := reloadTask(state).evaluated,
    stop := ((state) map { (state) => state.stop() }).value,
    restart := {
      val (cc, cf, cx) = (customConfiguration.value, configurationFiles.value, configurationXml.value)
      state.value.stop()
      state.value.start(
        addr = new InetSocketAddress(host.value, port.value),
        ssl = toSslSettings(ssl.value),
        logger = new FullLogger(state.value.log),
        apps = apps.value,
        customConf = cc,
        confFiles = cf,
        confXml = cx)
    },
    ServletKeys.test := {
      (stop in Configuration)
        .dependsOn(Keys.test in Test)
        .dependsOn(start in Configuration)
    }.value,
    customConfiguration := false,
    configurationFiles := Seq(),
    configurationXml := NodeSeq.Empty)

  def settings = globalSettings ++ inConfig(Configuration)(containerSettings)

  def pairToTask(conf: Configuration)(p: (String, ProjectReference)): Def.Initialize[Task[(String, Deployment)]] = {
    (deployment.in(p._2, conf)).map { (d) => (p._1, d) }
  }

  def deploy(map: (String, ProjectReference)*): SettingSeq = deploy(DefaultConf)(map: _*)

  def deploy(conf: Configuration)(map: (String, ProjectReference)*): SettingSeq = {
    settings ++ inConfig(Configuration)(Seq(
      apps := map.map(pairToTask(conf)).join.value))
  }

  def toSslSettings(sslConfig: Option[(String, Int, String, String, String)]): Option[SslSettings] = {
    sslConfig.map {
      case (sslHost, sslPort, keystore, password, keyPassword) =>
        SslSettings(
          addr = new InetSocketAddress(sslHost, sslPort),
          keystore = keystore,
          password = password,
          keyPassword = keyPassword)
    }
  }

  def discoverContexts(apps: Seq[(String, Deployment)]) = apps.map(_._1)

  def reloadParser: (State, Option[Seq[String]]) => Parser[String] = {
    import sbt.complete.DefaultParsers._
    (state, contexts) => {
      Space ~> token(NotSpace examples contexts.getOrElse(Seq.empty).toSet)
    }
  }

  def reloadTask(state: TaskKey[State]): Def.Initialize[InputTask[Unit]] = {
    val parser = loadForParser(discoveredContexts)(reloadParser)
    Def.inputTask {
      state.value.reload(parser.parsed)
    }
  }
}

package skinny.servlet

import sbt._
import sbt.internal.inc.classpath.ClasspathUtilities._
import java.net.InetSocketAddress
import java.lang.reflect.InvocationTargetException
import scala.xml.NodeSeq

object Runner {

  def runners = Seq(
    "skinny.servlet.Jetty9Runner")

  def packages = Seq("org.eclipse.jetty")

  def apply(classpath: Seq[File]): Runner = {
    val loader: ClassLoader = toLoader(classpath)
    val runner = guessRunner(loader, runners)
    runner.setLoader(loader)
    runner
  }

  def loadRunner(className: String, loader: ClassLoader): Runner = {
    Runners.makeInstance[Runner](loader, packages, className)
  }

  def guessRunner(loader: ClassLoader, rs: Seq[String]): Runner = rs match {
    case Seq() => sys.error("""Jetty dependencies should be on container classpath""")
    case Seq(runner, rest @ _*) =>
      try { loadRunner(runner, loader) }
      catch {
        case e: InvocationTargetException =>
          e.getCause match {
            case _: NoClassDefFoundError => guessRunner(loader, rest)
            case _: ClassNotFoundException => guessRunner(loader, rest)
            case t: Throwable => throw new RuntimeException("Something went wrong finding a runner", t)
          }
        case _: NoClassDefFoundError => guessRunner(loader, rest)
        case _: ClassNotFoundException => guessRunner(loader, rest)
        case t: Throwable => throw new RuntimeException("Something went wrong finding a runner", t)
      }
  }

}

trait Runner {

  protected var loader: ClassLoader = null

  def setLoader(loader: ClassLoader): Unit = {
    this.loader = loader
  }

  def start(
    addr: InetSocketAddress,
    ssl: Option[SslSettings],
    logger: AbstractLogger,
    apps: Seq[(String, Deployment)],
    customConf: Boolean,
    confFiles: Seq[File],
    confXml: NodeSeq): Unit

  def reload(context: String): Unit

  def stop(): Unit

  def join(): Unit

}

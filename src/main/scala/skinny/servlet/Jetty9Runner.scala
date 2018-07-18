package skinny.servlet

import java.net.InetSocketAddress

import org.eclipse.jetty.annotations.AnnotationConfiguration
import org.eclipse.jetty.plus.webapp.{ PlusConfiguration, EnvConfiguration }
import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.util.Scanner.BulkListener
import org.eclipse.jetty.util.resource.ResourceCollection
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.log.{ Logger => JLogger }
import org.eclipse.jetty.util.{ Scanner => JScanner }
import org.eclipse.jetty.webapp._
import org.eclipse.jetty.webapp.Configuration
import org.eclipse.jetty.xml.XmlConfiguration
import sbt._
import sbt.internal.inc.classpath.ClasspathUtilities.toLoader
import scala.collection.JavaConverters._

import scala.xml.NodeSeq

class Jetty9Runner extends Runner {

  def start(
    addr: InetSocketAddress,
    ssl: Option[SslSettings],
    logger: AbstractLogger,
    apps: Seq[(String, Deployment)],
    customConf: Boolean,
    confFiles: Seq[File],
    confXml: NodeSeq) {

    if (runningServer != null) {
      return
    }
    try {
      runningServer = new Server
      if (customConf) {
        configureCustom(confFiles, confXml)
      } else {
        configureConnector(addr)
        ssl match {
          case Some(s) => configureSecureConnector(s)
          case _ =>
        }
        configureContexts(apps)
      }
      runningServer.start()
    } catch {
      case t: Throwable =>
        runningServer = null
        throw t
    }
  }

  def reload(contextPath: String): Unit = {
    val (context, deployment) = contexts(contextPath)
    context.stop()
    setContextLoader(context, deployment.classpath)
    context.start()
  }

  def join(): Unit = {
    if (runningServer != null) {
      runningServer.join()
    }
  }

  def stop(): Unit = {
    if (runningServer != null) {
      runningServer.stop()
      runningServer.removeBeans()
      runningServer.clearAttributes()
      runningServer.destroy()
    }
    runningServer = null
  }

  // ------------------------------------------------------------------------------------------

  // ---
  // unused but don't delete these fields
  private[this] val _forceJettyLoad = classOf[Server]
  private[this] val _forceJettyLoad2 = classOf[NetworkTrafficServerConnector]
  // ---

  private[this] var runningServer: Server = null

  private[this] var contexts: Map[String, (WebAppContext, Deployment)] = Map()

  private[this] def setContextLoader(context: WebAppContext, classpath: Seq[File]) {
    val appLoader = toLoader(classpath, loader)
    context.setClassLoader(appLoader)
  }

  private[this] lazy val annotationConfigs: Seq[Configuration] =
    try {
      Seq(
        new EnvConfiguration,
        new PlusConfiguration,
        new AnnotationConfiguration)
    } catch {
      case e: NoClassDefFoundError => Seq.empty
    }

  private[this] lazy val configs: Seq[Configuration] = {
    Seq(
      new WebInfConfiguration,
      new WebXmlConfiguration,
      new MetaInfConfiguration,
      new FragmentConfiguration,
      new JettyWebXmlConfiguration) ++ annotationConfigs
  }

  private[this] def setEnvConfiguration(context: WebAppContext, file: File) {
    val config = new EnvConfiguration { setJettyEnvXml(file.toURI.toURL) }
    val array: Array[Configuration] = (configs ++ Seq(config)).toArray
    context.setConfigurations(array)
  }

  private[this] def deploy(contextPath: String, deployment: Deployment) = {
    import deployment._
    val context = new WebAppContext
    context.setContextPath(contextPath)

    if (Option(System.getProperty("os.name")) exists { x => x.toLowerCase.startsWith("windows") }) {
      context.getInitParams.put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
    }
    context.setBaseResource(new ResourceCollection(
      webappResources.map(_.getPath).toArray))
    setContextLoader(context, classpath)

    context.setExtraClasspath(classpath.map(_.getAbsolutePath).mkString(";"))
    env match {
      case Some(e) => setEnvConfiguration(context, e)
      case None => context.setConfigurations(configs.toArray)
    }
    webInfIncludeJarPattern.foreach(patttern =>
      context.setAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern", patttern))

    if (!scanDirectories.isEmpty) {
      val scanner = new Scanner(
        scanDirs = scanDirectories,
        scanInterval = scanInterval,
        thunk = () => reload(contextPath))
      scanner.start()
    }
    contexts += contextPath -> (context, deployment)
    context
  }

  private[this] def configureContexts(apps: Seq[(String, Deployment)]) {
    val contexts: Seq[WebAppContext] = apps.map {
      case (contextPath, deployment) => deploy(contextPath, deployment)
    }
    val coll = new ContextHandlerCollection()
    coll.setHandlers(contexts.toArray)
    runningServer.setHandler(coll)
  }

  private[this] def configureCustom(confFiles: Seq[File], confXml: NodeSeq) {
    confXml.foreach(x => new XmlConfiguration(x.toString).configure(runningServer))
    confFiles.foreach(f => new XmlConfiguration(f.toURI.toURL).configure(runningServer))
  }

  private[this] def configureConnector(addr: InetSocketAddress) {
    val conn = new ServerConnector(runningServer)
    conn.setHost(addr.getAddress.getHostAddress)
    conn.setPort(addr.getPort)
    runningServer.addConnector(conn)
  }

  private[this] def configureSecureConnector(ssl: SslSettings) {
    val context = new SslContextFactory()
    context.setKeyStorePath(ssl.keystore)
    context.setKeyStorePassword(ssl.password)
    val conn = new ServerConnector(runningServer, context)
    conn.setHost(ssl.addr.getAddress.getHostAddress)
    conn.setPort(ssl.addr.getPort)
    runningServer.addConnector(conn)
  }

  private class DelegatingLogger(delegate: AbstractLogger) extends LoggerBase(delegate) with JLogger {
    def getLogger(name: String) = this
    def debug(x: String, y: Long): Unit = ()
  }

  private class Scanner(scanDirs: Seq[File], scanInterval: Int, thunk: () => Unit) extends JScanner {
    setScanDirs(scanDirs.asJava)
    setRecursive(true)
    setScanInterval(scanInterval)
    setReportExistingFilesOnStartup(false)
    addListener(new BulkListener {
      def filesChanged(files: java.util.List[String]) { thunk() }
    })
  }

}


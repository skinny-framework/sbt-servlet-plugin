package skinny.servlet

import java.net.InetSocketAddress

case class SslSettings(
  addr: InetSocketAddress,
  keystore: String,
  password: String,
  keyPassword: String)

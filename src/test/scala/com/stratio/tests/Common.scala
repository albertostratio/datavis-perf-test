package com.stratio.tests

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.http.Predef.http
import org.slf4j.LoggerFactory

trait Common {

  def logger = LoggerFactory.getLogger(this.getClass)

  val sut = "http://".concat(System.getProperty("SUT", "148.251.128.43:19000"))

  val jdbcds = System.getProperty("JDBCDS", "127.0.0.1")
  val mongods = System.getProperty("MONGODS", "127.0.0.1")
  val cassandrads = System.getProperty("CASSANDRADS", "127.0.0.1")
  val elasticsearchds = System.getProperty("ELASTICSEARCHDS", "127.0.0.1")
  val restds = System.getProperty("RESTDS", "127.0.0.1")


  val httpConf = http
    .baseURL(sut)
    .warmUp(sut)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36")

  object order{
    val dataStart = new AtomicInteger(1)
  }
}

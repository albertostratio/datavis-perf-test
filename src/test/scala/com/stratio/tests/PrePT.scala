package com.stratio.tests

import java.util

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{ http, jsonPath }
import io.gatling.http.request._
import java.io._

class PrePT extends Simulation with Common{

  val writer = new PrintWriter(new File("associationId.csv" ))

  object RequiredElement {

    lazy val assocEndpoint = "/pages/".concat("""${PID}""").concat("/widgets/").concat("""${WID}""")

    val getUser = exec(http("GET /profile/test")
        .get("/profile/test")
        .check(jsonPath("$.id")
          .saveAs("UID")))
      .exitHereIfFailed

    val createDataSource = exec(http("POST /datasources")
        .post("/datasources")
        .body(ELFileBody("DS.txt")).asJSON
        .check(jsonPath("$.dataSource.id")
          .saveAs("DSID")))
      .exitHereIfFailed

    val createDataView = exec(http("POST /dataviews")
        .post("/dataviews")
        .body(ELFileBody("DV.txt")).asJSON
        .check(jsonPath("$.dataView.id")
          .saveAs("DVID")))
      .exitHereIfFailed

    val createWidget = exec(http("POST /widgets")
        .post("/widgets")
        .body(ELFileBody("W.txt")).asJSON
        .check(jsonPath("$.widget.id")
          .saveAs("WID")))
      .exitHereIfFailed

    val createPage = exec(http("POST /pages")
        .post("/pages")
        .body(ELFileBody("P.txt")).asJSON
        .check(jsonPath("$.page.id")
          .saveAs("PID")))
      .exitHereIfFailed

    val assocPageWidget = exec(http("POST /pages/X/widgets/Y")
        .post(assocEndpoint)
        .body(ELFileBody("PW.txt")).asJSON
        .check(jsonPath("$.pageWidget.associationId")
          .saveAs("PWID")))
      .exec(
        session => {
          logger.info("Adding pageWidget.associationId, value {}  to feeder", session("PWID").as[String])
          val el = session.attributes.get("DS")
          val element = el.get.asInstanceOf[util.LinkedHashMap[String,String]]
          val elementType = element.get("type").get
          writer.write(elementType + "," + session("PWID").as[String] + "\n")
          session })
      .exitHereIfFailed
  }

  before {
     writer.append("DS,PWID" + "\n")
  }

  after {
    writer.close()
    logger.info("Preconditions done")
  }

  val feeder = jsonFile("sources.json")

  val users = scenario("Pre-requirements")
    .foreach(feeder.records, "record") {
       exec(flattenMapIntoAttributes("${record}"))
      .exec(
        RequiredElement.getUser
        ,RequiredElement.createDataSource
        ,RequiredElement.createDataView
        ,RequiredElement.createWidget
        ,RequiredElement.createPage
        ,RequiredElement.assocPageWidget
      )}

  setUp(
    users
      .inject(atOnceUsers(1))
  ).protocols(httpConf)
}
package com.stratio.tests

import java.util.Scanner

import io.gatling.core.Predef._
import io.gatling.http.Predef. { RawFileBodyPart, ELFileBodyPart, ELFileBody, http, jsonPath }
import io.gatling.http.request.BodyPart. { rawFileBodyPart, elFileBodyPart }
import java.io._

class PrePT extends Simulation with Common {

  val writer = new PrintWriter(new File("associationId.csv" ))

  object RequiredElement {

    //TODO: pick up the widget id from an endpoint
    val widgetId = "14"
    lazy val assocEndpoint = "/pages/".concat("""${PID}""").concat("/widgets/").concat(widgetId)


    val authenticateUser = exec(http("POST /login/authenticate/userpass")
      .post("/login/authenticate/userpass")
      .body(ELFileBody("AUTH.txt")).asJSON
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
          val element = el.get.asInstanceOf[java.util.LinkedHashMap[String,String]]
          writer.write(element.get("type").get + "," + session("PWID").as[String] + "\n")
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

  //replace REST_DS, JDBC_DS, MONGO_DS, CASSANDRA_DS from sources.json
  val placeholder = new Scanner(new File("target/test-classes/sources_placeholders.json"))
  val replaced = new PrintWriter("target/test-classes/sources.json")
  var sources = placeholder.useDelimiter("\\A").next()

  sources = sources.replaceAll("JDBCDS", jdbcds)
  sources = sources.replaceAll("MONGODS", mongods)
  sources = sources.replaceAll("CASSANDRADS", cassandrads)
  sources = sources.replaceAll("ELASTICSEARCHDS", elasticsearchds)
  sources = sources.replaceAll("RESTDS", restds)
  sources = sources.replaceAll("SPARKCASSANDRADS", sparkcassandrads)
  sources = sources.replaceAll("DEEPDS", deepds)

  replaced.println(sources)
  placeholder.close()
  replaced.close()

  val feeder = jsonFile("sources.json")

  val users = scenario("Pre-requirements")
    .foreach(feeder.records, "record") {
      exec(flattenMapIntoAttributes("${record}"))
      .exec(
        RequiredElement.authenticateUser
        ,RequiredElement.createDataSource
        ,RequiredElement.createDataView
        ,RequiredElement.createPage
        ,RequiredElement.assocPageWidget
      )}

  setUp(
    users
      .inject(atOnceUsers(1))
  ).protocols(httpConf)
}

package com.stratio.tests

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{StringBody, http, responseTimeInMillis, jsonPath}
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class DataPT extends Simulation with Common{

  object Data {

    val getData =
      forever(
        pace(2 seconds, 4 seconds)
        .exec(http("POST /data")
          .post("/data")
          .body(StringBody(
          """{"id": ${PWID}
            ,"filters": []
            |,"fetchMetaData": false
            |}""".stripMargin)).asJSON
          .check(jsonPath("$")
          .saveAs("response"))
          .check(responseTimeInMillis.lessThanOrEqual(500L))
        )
  )}

  val feederAssoc = csv("associationId.csv")

  val scns = new ListBuffer[ScenarioBuilder]()

  feederAssoc.records.foreach(fA => {
    scns += scenario(fA.get("DS").get)
      .exec(flattenMapIntoAttributes(fA))
      .exec(Data.getData)
  })


  logger.error("Scenarios size: {}",scns.size )
  if (scns.size < 1) {
    throw new AssertionError("No scenarios")
  }

  setUp(
    scns.toList.map(_.inject(rampUsersPerSec(5) to (200) during (5 minutes)))
  )
  .maxDuration(30 minutes)
  .uniformPauses(2)
  .protocols(httpConf)
  .assertions(
    global.responseTime.max.lessThan(50),
    global.successfulRequests.percent.greaterThan(95)
  )

}
package com.stratio.tests

import io.gatling.core.Predef._
import scala.concurrent.duration._

trait DataCommon extends PerformanceTest{

  feederAssoc.records.foreach(fA => {
    if (fA.get("DS").get.equals(dtype)) {
      scns += scenario(dtype.toUpperCase)
        .exec(flattenMapIntoAttributes(fA))
        .exec(Data.getData)
    }
  })

  setUp(
    scns.toList.map(_.inject(rampUsers(users) over (new DurationInt(injectDuration).minutes))))
    .maxDuration(new DurationInt(runDuration).minutes)
    .uniformPauses(5)
    .protocols(httpConf)
    .assertions(
      global.responseTime.max.lessThan(3000),
      global.successfulRequests.percent.greaterThan(95)
    )

}


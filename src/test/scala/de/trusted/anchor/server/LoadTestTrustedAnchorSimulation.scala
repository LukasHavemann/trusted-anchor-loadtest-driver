package de.trusted.anchor.server

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt

//noinspection TypeAnnotation
class LoadTestTrustedAnchorSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8080")

  val scn = scenario("sign hash")
    .exec(http("sign hash")
      .get("/sign/hash/8effc8acf8ebfa15a11efbb4e1a62b3e7cd64f630f3860362361e9e3f064c858?appName=app&eventId=1"))

  setUp(
    scn.inject(rampUsers(50).during(5.seconds),
      constantUsersPerSec(200).during(30.seconds))
      .protocols(httpProtocol))
}
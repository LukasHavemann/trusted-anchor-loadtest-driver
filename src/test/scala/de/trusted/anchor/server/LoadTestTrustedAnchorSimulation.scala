package de.trusted.anchor.server

import io.gatling.commons.util.Hex
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.tsp.{TSPAlgorithms, TimeStampRequest, TimeStampRequestGenerator}

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import scala.concurrent.duration.DurationInt

//noinspection TypeAnnotation
class LoadTestTrustedAnchorSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8080")

  val request: TimeStampRequest = createRequest()

  def createRequest(): TimeStampRequest = {
    val hashAsBytes = Hex.fromHexString("8effc8acf8ebfa15a11efbb4e1a62b3e7cd64f630f3860362361e9e3f064c84e")
    val timeStampRequestGenerator = new TimeStampRequestGenerator()
    timeStampRequestGenerator.addExtension(
      new ASN1ObjectIdentifier("1.3.6.1.4.1.1"),
      false,
      "test".getBytes(StandardCharsets.UTF_8)
    )
    timeStampRequestGenerator.addExtension(
      new ASN1ObjectIdentifier("1.3.6.1.4.1.2"),
      false,
      ByteBuffer.allocate(4).putInt(1).array()
    )

    timeStampRequestGenerator.generate(TSPAlgorithms.SHA256, hashAsBytes)
  }

  val standardRestApi = scenario("sign hash over standard rest api")
    .exec(http("sign standard rest hash")
      .get("/sign/hash/8effc8acf8ebfa15a11efbb4e1a62b3e7cd64f630f3860362361e9e3f064c858?appName=app&eventId=1"))

  val rfc3161RestApi = scenario("sign hash over RFC 3161")
    .exec(http("sign standard rest hash")
      .post("/sign/hash/")
      .body(ByteArrayBody {
        session => request.getEncoded
      })
      .check(status.is(200)))

  setUp(
    rfc3161RestApi.inject(rampUsers(50).during(5.seconds),
      constantUsersPerSec(10).during(30.seconds))
      .protocols(httpProtocol))
}
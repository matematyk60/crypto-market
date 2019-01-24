package com.market.e2e.specs

import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  RequestEntity,
  StatusCodes
}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.native.JsonMethods.parse
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, JValue, Serialization}
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps
import scala.xml.{Elem, XML}

trait RestSpec extends FlatSpec with ScalatestRouteTest with Json4sSupport {
  protected implicit val timeout: RouteTestTimeout = RouteTestTimeout(5 seconds)
  protected implicit val formats: DefaultFormats = DefaultFormats
  protected implicit val serialization: Serialization = Serialization

  protected val Statuses: StatusCodes.type = StatusCodes

  protected def responseAsJson(
      implicit timeout: FiniteDuration = 5 seconds): JValue =
    parse(responseAsString)

  protected def responseAsString(
      implicit timeout: FiniteDuration = 5 seconds): String =
    Await.result(responseEntity.toStrict(timeout).map(_.data.utf8String),
                 timeout)

  protected def errors: List[String] =
    (responseAsJson \ "errors").extract[List[String]]

  implicit class JsonSerializingSupport(value: AnyRef) {
    def toJsonEntity: RequestEntity =
      HttpEntity(ContentTypes.`application/json`, serialization.write(value))
  }

  protected implicit var endpoint: Route = _

  protected def createEndpoint(route: Route): Unit =
    endpoint = route

}

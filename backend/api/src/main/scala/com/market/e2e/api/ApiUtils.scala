package com.market.e2e.api

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import cats.data.NonEmptyList
import com.market.shared.Utils._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, Serialization}
import org.json4s.native.Serialization

trait ApiUtils extends Json4sSupport {
  protected implicit val formats: Formats = DefaultFormats
  protected implicit val serialization: Serialization = Serialization

  protected def errorsResponse(errors: List[String],
                               statusCode: StatusCode =
                                 StatusCodes.UnprocessableEntity)
    : (StatusCode, Map[String, List[String]]) =
    statusCode -> Map("errors" -> errors)

  protected def idResponse[T](
      name: String,
      id: T,
      status: StatusCode = StatusCodes.OK): ToResponseMarshallable =
    status -> Map(name -> id)
  protected def emptyOkResponse: ToResponseMarshallable =
    StatusCodes.OK -> Map.empty

  def errorToJson(error: Any) = error.toSnakeCaseClassName
}

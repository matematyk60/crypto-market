package com.market.e2e.api.core

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import com.market.e2e.api.ApiUtils
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait Endpoint extends ApiUtils with Directives with StrictLogging {

  protected val Statuses: StatusCodes.type = StatusCodes
  def routes: Seq[Route]
  def flattenedRoute: Route = routes.reduce(_ ~ _)

  def completeExistingResult(errorStatus: StatusCode = Statuses.NotFound)(
      result: Future[Option[Done]]): Route =
    onSuccess(result) {
      case Some(Done) => complete(emptyOkResponse)
      case None       => complete(errorStatus)
    }
}

object Endpoint extends StrictLogging {

  def start(host: String, port: Int, routes: Route)(
      implicit mat: ActorMaterializer,
      system: ActorSystem,
      ec: ExecutionContext): Unit =
    Http()
      .bindAndHandle(routes, host, port)
      .onComplete {
        case Success(_)  => logger.info(s"API started at $host:$port}")
        case Failure(ex) => logger.error(s"Cannot bind API to $host:$port}", ex)
      }

}

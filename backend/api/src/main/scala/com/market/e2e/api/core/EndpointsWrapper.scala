package com.market.e2e.api.core

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LoggingMagnet}
import akka.http.scaladsl.server.{
  ExceptionHandler,
  RejectionHandler,
  RouteResult
}
import akka.http.scaladsl.settings.RoutingSettings
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpjson4s.Json4sSupport

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps
import scala.util.control.NonFatal

class EndpointsWrapper(endpoints: Endpoint*)(implicit system: ActorSystem,
                                             mat: ActorMaterializer,
                                             ec: ExecutionContext)
    extends Json4sSupport
    with StrictLogging {

  private val exceptionHandler = ExceptionHandler {
    case NonFatal(e) ⇒
      ctx ⇒
        {
          ctx.log.error(
            e,
            "Error during processing of request: '{}'. Completing with {} response.",
            ctx.request,
            InternalServerError)
          ctx.complete(InternalServerError)
        }
  }

  private val routingSettings = RoutingSettings(system.settings.config)
  private val rejectionHandler = RejectionHandler.default

  private val allowedCorsMethods = Seq(GET, POST, PUT, DELETE, OPTIONS)

  private val corsSettings =
    CorsSettings.defaultSettings.withAllowedMethods(allowedCorsMethods)

  def routingWithSupport: server.Route =
    (pathSingleSlash & options)(complete(OK)) ~
      (path("status") & get)(complete(OK)) ~
      (cors(corsSettings) & handleRejections(rejectionHandler) & handleExceptions(
        exceptionHandler.seal(routingSettings))) {
        extractRequest(
          request =>
            DebuggingDirectives.logResult(
              LoggingMagnet(logErrorStatuses(request)))(
              endpoints.map(_.flattenedRoute).reduce(_ ~ _)))
      }

  private def logErrorStatuses(request: HttpRequest)(
      loggingAdapter: LoggingAdapter)(res: RouteResult): Unit = res match {
    case RouteResult.Complete(response)
        if response.status == UnprocessableEntity =>
      val errorResponse = Await.result(
        response.entity.toStrict(1 second).map(_.data.utf8String),
        1 second)
      loggingAdapter.debug(
        s"FOR REQUEST: {} , ENCOUNTERED SOME DOMAIN PROBLEMS {}",
        request,
        errorResponse)
    case _ => // pass
  }
}

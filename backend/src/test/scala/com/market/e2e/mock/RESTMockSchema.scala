package com.market.e2e.mock

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Serialization}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

abstract class RESTMockSchema(port: Int, host: String = "127.0.0.1")
    extends Json4sSupport
    with Directives {

  protected implicit val serialization: Serialization = Serialization
  protected implicit val formats: DefaultFormats = DefaultFormats
  protected val Statuses: StatusCodes.type = StatusCodes
  private var binding: Option[Http.ServerBinding] = None

  def routing: Route

  def start(implicit system: ActorSystem, mat: ActorMaterializer): Unit =
    binding = Some(
      Await.result(Http().bindAndHandle(routing, host, port), 5 seconds))

  def stop(): Unit =
    binding.foreach(binding => Await.ready(binding.unbind(), 5 seconds))

  def reset(): Unit = ()

}

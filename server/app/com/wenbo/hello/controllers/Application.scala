package com.wenbo.hello.controllers

import javax.inject._
import akka.NotUsed
import akka.actor._
import akka.event.Logging
import akka.http.scaladsl.model.ws.Message
import akka.pattern.ask
import akka.stream.{FlowShape, Materializer, UniqueKillSwitch, scaladsl}
import akka.stream.scaladsl._
import akka.util.Timeout
import play.api.Logger
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import views.ConvertHtml

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationConversions
import scala.concurrent.duration.Duration
import scala.concurrent.duration.span
import scala.concurrent.duration.TimeUnit
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) with SameOriginCheck {

  val logger = play.api.Logger(getClass)

  implicit val timeout = Timeout(2 second)

  def index = Action {
    Ok(views.html.index(""))
  }

  def index2(st: String) = Action {
    Ok(views.html.index(""))
  }
  import GraphDSL.Implicits._

  ActorFlow

  def convert = Action {
    Ok(ConvertHtml.getHtml).as(HTML)
  }

  private val helloSource = Source.single("Hello!")
  private val logSink = Sink.foreach { s: String => logger.info(s"received: $s") }

  def fromSinkAndSource[I, O](s: I) = println(s)

  def hello =  WebSocket.accept[String, String] { request =>

    // log the message to stdout and send response back to client
    Flow[String].map { msg =>
      println(msg)
      "I received your message: " + msg
    }
  }

  def socket: WebSocket = WebSocket.acceptOrResult[String, String] { _ =>
    val closeAfterMessage = false

//    close.getOrElse(false)
    Future.successful(Right(Flow.fromSinkAndSource(
      logSink,
      // server close connection after sending message
      if (closeAfterMessage) helloSource
      // keep connection open
      else helloSource.concat(Source.maybe)

    )))

  }

  def path(path: String) = Action {
    Ok(ConvertHtml.getHtml).as(HTML)
  }
}
trait SameOriginCheck {

  def logger: Logger

  /**
    * Checks that the WebSocket comes from the same origin.  This is necessary to protect
    * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
    *
    * See https://tools.ietf.org/html/rfc6455#section-1.3 and
    * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
    */
  def sameOriginCheck(rh: RequestHeader): Boolean = {
    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        logger.debug(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        logger.error(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
        false

      case None =>
        logger.error("originCheck: rejecting request because no Origin header found")
        false
    }
  }

  /**
    * Returns true if the value of the Origin header contains an acceptable value.
    *
    * This is probably better done through configuration same as the allowedhosts filter.
    */
  def originMatches(origin: String): Boolean = {
//    origin.contains("localhost:9000") || origin.contains("localhost:19001")
    true
  }

}
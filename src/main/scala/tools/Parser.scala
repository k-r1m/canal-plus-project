package tools

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink}
import akka.util.ByteString
import com.newmotion.akka.rabbitmq.{Channel, ChannelMessage}
import main.scala.PublishSubscribeMoviesChannel.{exchange, system}
import models.TitleMovies
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}

trait Parser[T] {
  def apply(s: String): Either[String, T]
}

trait TryParser[T] extends Parser[T] {

  protected def parse(s: String): T

  def apply(s: String): Either[String, T] =
    Try(parse(s)).transform(
      s => Success(Right(s)),
      f => Success(Left(f.getMessage))).get
}

object Parsers {

  implicit val stringParser: Parser[String] = new Parser[String] {
    def apply(s: String): Either[String, String] = Right(s)
  }

}

object MovieParsers {

  implicit val runTimeParser = new TryParser[Int] {
    protected def parse(s: String): Int = s match {
      case "\\N" => 0
      case _ => s.toInt
    }
  }

  implicit val endYearParser = new TryParser[Int] {
    protected def parse(s: String): Int = s match {
      case "\\N" => 0
      case _ => s.toInt
    }
  }
}

object TSVParser {
  def dataToTitleMovie(values: Array[String]): TitleMovies = {
    TitleMovies(values(0),
      values(1),
      values(2),
      values(3),
      values(4) match {
        case "0" => false
        case "1" => true
      },
      values(5),
      MovieParsers.endYearParser(values(6)).getOrElse(0),
      values(7),
      values(8).split(",").toSeq)
  }

  def parseAndPublishMovie(file: String, genre: String = "comedy") = {
    implicit val sys = ActorSystem("Sys")
    implicit val mat = Materializer(sys)

    val flow = Flow[String].map(m => {
      val values = m.split("\\t").map(_.trim)
      dataToTitleMovie(values)
    })

    val flowFilteredGenre = Flow[TitleMovies].filter(tm =>
      tm.titleType.equals("movie")
        && tm.genres.map(_.toLowerCase).contains(genre))

    val sink = Sink.foreach[TitleMovies](m => {
      val publisher = system.actorSelection("/user/canal-plus-movies/publish-movies")

      def publish(channel: Channel) {
        channel.basicPublish(exchange, "", null, m.originalTitle.getBytes("UTF-8"))
      }

      publisher ! ChannelMessage(publish, dropIfNoChannel = false)
    })

    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    FileIO.fromPath(Paths.get(file))
      .via(
        Framing.delimiter(
          ByteString("\n"),
          4096,
          true).map(_.utf8String).drop(1))
      .via(flow)
      .via(flowFilteredGenre)
      .runWith(sink)
      .andThen {
        case _ =>
          system.terminate()
          Await.ready(system.whenTerminated, 1 minute)
      }
  }
}
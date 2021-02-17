package services

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink}
import akka.util.ByteString
import com.newmotion.akka.rabbitmq.{Channel, ChannelMessage}
import models.TitleMovies
import services.PublishSubscribeMoviesChannel.{exchange, system}
import tools.MovieParsers

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.Await


object ProcessTSVFile {
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
      .via(flow) // parse every line into TitleMovie object
      .via(flowFilteredGenre) // Filter by comedy genre
      .runWith(sink)
      .andThen {
        case _ =>
          system.terminate()
          Await.ready(system.whenTerminated, 1 minute)
      }
  }
}

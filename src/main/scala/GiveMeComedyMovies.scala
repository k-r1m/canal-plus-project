package main.scala

import tools.TSVParser

object GiveMeComedyMovies {
  def main(args: Array[String]): Unit = {
    PublishSubscribeMoviesChannel
    if (args.size == 1)
      TSVParser.parseAndPublishMovie(args(0))
    else TSVParser.parseAndPublishMovie(args(0), args(1))
  }

}

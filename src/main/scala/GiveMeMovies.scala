package main.scala

import services._

object GiveMeMovies {
  def main(args: Array[String]): Unit = {
    if (args.size < 1)
      println("Please give a path of TSV file as an argument")
    else {
      PublishSubscribeMoviesChannel
      if (args.size == 1)
        ProcessTSVFile.parseAndPublishMovie(args(0))
      else ProcessTSVFile.parseAndPublishMovie(args(0), args(1))
    }
  }

}

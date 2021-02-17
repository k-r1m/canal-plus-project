package tools

import models.TitleMovies

object GiveMeMovieByGenre {
  def byCustomGenre(genre: String, titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.map(_.toLowerCase).contains(genre))
  }

  def byMultipleGenres(genre1: String, genre2: String, titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.map(_.toLowerCase).forall(Seq(genre1, genre2).contains))
  }

  def byComedy(titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.contains("Comedy"))
  }

  def byAction(titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.contains("Action"))
  }

  def byAnimation(titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.contains("Animation"))
  }

  def byDrama(titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.contains("Drama"))
  }

  def byRomance(titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.contains("Romance"))
  }

  def byFantasy(titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.contains("Fantasy"))
  }

  def byHorror(titleMovies: Seq[TitleMovies]): Seq[TitleMovies] = {
    titleMovies.filter(tm => tm.titleType.equals("movie") && tm.genres.contains("Horror"))
  }

}

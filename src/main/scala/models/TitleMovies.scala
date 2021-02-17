package models

case class TitleMovies(tconst: String,
                       titleType: String,
                       primaryTitle: String,
                       originalTitle: String,
                       isAdult: Boolean,
                       startYear: String,
                       endYear: Int,
                       runtimeMinutes: String,
                       genres: Seq[String]
                      )
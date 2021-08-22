package util

import slick.jdbc.{ JdbcProfile, PostgresProfile, SQLiteProfile }
import play.api.Configuration

sealed trait Environment {
  val dbProfile: JdbcProfile
}

object Environment {
  def select(config: Configuration): Environment =
    config.getOptional[String]("happening.stage") match {
      case Some("ci") => CI
      case Some("production") => Production
      case _ => Development
    }

  object Development extends Environment {
    val dbProfile: JdbcProfile = SQLiteProfile
  }

  object CI extends Environment {
    val dbProfile: JdbcProfile = PostgresProfile
  }

  object Production extends Environment {
    val dbProfile: JdbcProfile = PostgresProfile
  }
}
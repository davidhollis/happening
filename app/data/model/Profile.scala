package data.model

import java.net.URL
import java.time.ZonedDateTime

import data.{ HasID, ID, Identifiable }

/**
  * A profile is a set of biographical information that can be referenced in an RSVP or other event
  * relation. Only the selected profile will be visible, and only to the audience its owner elects
  * to reveal it to (the Event organizers or Group owners, at a minimum).
  *
  * A [[User]] may have multiple Profiles. This allows them to control how they present on an
  * Event-by-Event basis. Even when another person is allowed to see a Profile, they cannot see any
  * other Profiles owned by the same User, or even how many other profiles there may be. In this
  * way, a User's freedom to present differently in different contexts, as well as their privacy in
  * doing so, is preserved.
  */
case class Profile(
  user: ID[User],
  displayName: String,
  fullName: String,
  pronouns: String,
  photoUrl: URL,
  lastUsed: Option[ZonedDateTime],
  id: ID[Profile] = ID.random,
) extends HasID[Profile]

object Profile {
  implicit val identifiable: Identifiable[Profile] = Identifiable("profile")

  val commonPronounsEN: Seq[String] = Seq(
    "they/them",
    "she/her",
    "he/him",
    "she/they",
    "he/they",
    "they/she",
    "they/he",
  )
}
package data.model

import java.time.ZonedDateTime

import data.{ HasID, ID, Identifiable }

/**
  * A Series is a way of grouping related/regular [[Event]]s.
  * For example, a weekly movie night might have a Series so regular attendees can follow, and the
  * organizer doesn't have to invite everyone individually to each night.
  */
case class Series(
  name: String,
  description: String,
  owner: ID[User],
  followingRequiresApproval: Boolean,
  id: ID[Series] = ID.random,
) extends HasID[Series]

object Series {
  implicit val identifiable: Identifiable[Series] = Identifiable("series")
}

/**
  * Join model representing that the specified [[Event]] belongs to the specified [[Series]].
  * The association is many-to-many: an Event may belong to multiple Series, and a Series
  * generally will have multiple events; and it is unique by (event, series).
  */
case class EventInSeries(
  event: ID[Event],
  series: ID[Series],
)

/**
  * Join model representing that the specified [[User]] follows (or has requested to follow) the
  * specified [[Series]].
  * The association is many-to-many and unique by (user, series).
  * The [[Profile]] used does not restrict in any way the profile(s) the User may sign up for
  * events with, and it is only visible to the user and to the owner of the Series. The User may
  * change which profile is associated with their Series membership at any time.
  *
  * In the (ideally impossible) event that this model links to a profile which does not belong to
  * the user in question, the profile is visible to nobody.
  */
case class FollowingSeries(
  user: ID[User],
  profile: ID[Profile],
  series: ID[Series],
  requested: ZonedDateTime,
  approved: Option[ZonedDateTime],
)

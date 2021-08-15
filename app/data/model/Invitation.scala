package data.model

import java.time.ZonedDateTime

import data.{ HasID, ID, Identifiable }

/**
  * Join model representing that the specified [[User]] has been invited to the specified
  * [[Event]]. This is only visible to the invited User and to the Event's organizers, but anyone
  * who can see the event page can see a count of the number of people invited.
  *
  * Invitations that are actioned become [[Response]]s: that is, a new Response is created, and the
  * original Invitation is deleted.
  */
case class Invitation(
  user: ID[User],
  event: ID[Event],
  /** If an event organizer invites someone explicitly by contact method, this will contain that
    * contact info. It's visible only to event organizers, for their own reference.
    */
  invitedAs: Option[String],
  /** True if the user is being invited to be an organizer on a draft event.
    * Ignored for non-draft events (i.e., if an event is [[Event.Status.Open]], an Invitation with
    * `organizer = true` is identical to one with `organizer = false`).
    */
  organizer: Boolean,
  created: ZonedDateTime,
  id: ID[Invitation] = ID.random,
) extends HasID[Invitation]

object Invitation {
  implicit val identifiable: Identifiable[Invitation] = Identifiable("invitation")
}
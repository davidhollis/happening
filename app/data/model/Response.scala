package data.model

import java.time.ZonedDateTime

import data.{ Enum, HasID, ID, Identifiable }

/**
  * An indication of a [[User]]'s intention for an [[Event]].
  * Responses are unique by (user, event). All fields are visible to event organizers unless the
  * status is [[Response.Status.RemindMe]]. All fields are visible to and editable by the User.
  *
  * The [[Profile]] data's visibility is controlled by `profileVisibility`. When viewing an event
  * page, any Responses with Profiles the viewer is not permitted to see appear with an anonymized
  * avatar unique to the (user, event) pair.
  *
  * Responses with a status of [[Response.Status.Noncommittal]] do not appear to anyone beyond the
  * User they relate to and the event organizers. Those exist only to associate a Profile with an
  * Event for the purposes of [[Post]]s or [[Comment]]s.
  */
case class Response(
  user: ID[User],
  /** The profile the owning user wishes to present as to the audience indicated by
    * `profileVisibility.
    *
    * In the (ideally impossible) event that this model links to a profile which does not belong to
    * the user in question, the profile is visible to nobody.
    */
  profile: ID[Profile],
  event: ID[Event],
  status: Response.Status,
  profileVisibility: Response.Visibility,
  /** Any additional information the User wishes to express to the organizers. */
  comment: String,
  /** The number of additional guests the User is bringing.
    *
    * This is visible only to the organizers and the responding User.
    *
    * The frontend will prevent this from being set greater than the [[Event.maxGuests]] at the
    * time the response is created or edited, but it's still possible to construct scenarios where
    * a Response's `additionalGuests` exceeds its Event's `maxGuests`. When this becomes true, the
    * organizers will be notified. As long as it is true, the organizers and the repsonding user
    * will see a warning message on the event page.
    */
  additionalGuests: Int,
  /** Answers to the [[Event.responseQuestions]], as a map of `(question -> answer)`. These are
    * visible only to the organizer and responding User.
    */
  answersToEventQuestions: Map[String, String],
  created: ZonedDateTime,
  updated: ZonedDateTime,
  id: ID[Response] = ID.random,
) extends HasID[Response]

object Response {
  implicit val identifiable: Identifiable[Response] = Identifiable("response")

  sealed abstract class Status(override val toString: String)

  object Status extends Enum[Status] {
    case object Organizer extends Status("organizer")
    case object Going extends Status("going")
    case object Waitlisted extends Status("waitlisted")
    case object Maybe extends Status("maybe")
    case object NotGoing extends Status("not_going")
    case object Request extends Status("request")
    case object Noncommittal extends Status("noncommittal")

    val all: Set[Status] = Set(
      Organizer,
      Going,
      Waitlisted,
      Maybe,
      NotGoing,
      Request,
      Noncommittal,
    )
    override val default: Option[Status] = Some(Noncommittal)
  }

  sealed abstract class Visibility(
    override val toString: String,
    val description: String,
  )

  object Visibility extends Enum[Visibility] {
    case object OrganizersOnly extends Visibility(
      "organizers_only",
      description = "Only event organizers.",
    )
    case object AttendeesOnly extends Visibility(
      "attendees_only",
      description = "Only people who are attending the event.",
    )
    case object Anyone extends Visibility(
      "anyone",
      description = "Anyone who can see the event page.",
    )

    val all: Set[Visibility] = Set(
      OrganizersOnly,
      AttendeesOnly,
      Anyone,
    )
    override val default: Option[Visibility] = Some(OrganizersOnly)
  }
}

/** A request from a [[User]] to be notified of an upcoming [[Event]]. */
case class Reminder(
  user: ID[User],
  event: ID[Event],
  /** The time at which the user will receive a reminder for this event, if any.
    * Reminders ignore notification settings because they're explicitly opt-in. They will send the
    * notification method to all preferred [[ContactMethod]]s of the User.
    */
  reminderTime: Option[ZonedDateTime],
  /** Whether the reminder has been sent. This Reminder will be sent only once. */
  reminderSent: Boolean,
)
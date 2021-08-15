package data.model

import java.net.URL
import java.time.ZonedDateTime

import data.{ Enum, HasID, ID, Identifiable }

/**
  * An Event is a gathering which will (hopefully!) be attended by multiple [[User]]s.
  *
  * An Event has one or more organizers, at least one of which is the creator. If an event ever
  * drops to zero organizers, it'll be [[Event.Status.Cancelled]] automatically.
  */
case class Event(
  /** Whether this event page is a draft, is open for RSVPs, or has been cancelled.
    * Organizers may only be added while the event in is a [[Event.Status.Draft]] state (see
    * [[Event.State]] for the reasoning behind this decision).
    */
  status: Event.Status,
  /** The title of the event. */
  title: String,
  /** The description of the event. Markdown is supported. */
  description: String,
  /** The URL of the event's cover image. */
  coverImageURL: URL,
  /** The event start time, including time zone. */
  startTimestamp: ZonedDateTime,
  /** The event end time, including time zone. */
  endTimestamp: ZonedDateTime,
  /** The physical location of the event, if any.
    * If provided, this will display as a link to Google Maps.
    */
  physicalLocation: Option[String],
  /** A URL (e.g., Zoom) to join the event, if any. */
  joinURL: Option[URL],
  /** Who can see the event page. */
  visibility: Event.Visibility,
  /** Who is allowed to RSVP for the event. */
  joinability: Event.Joinability,
  /** The maximum number of extra guests an invitee can bring.
    * Generally this will either be zero (no guests) or one (invitees can bring a +1).
    */
  maxGuests: Int,
  /** The maximum total number of invitees and guests.
    * Once this limit is reached, subsequent people who RSVP "yes" will be placed on a waitlist.
    */
  maxAttendance: Option[Int],
  /** Who is allowed to post discussion topics on the event page.
    * Each post sets who can comment individually, so it's possible to lock down toplevel posts
    * while still allowing open discussion.
    */
  postability: Event.Postability,
  /** A list of questions attendees who RSVP "yes" will be asked.
    * Answers to these questions are only visible to event organizers.
    */
  rsvpQuestions: Seq[String],
  id: ID[Event] = ID.random,
) extends HasID[Event]

object Event {
  implicit val identifiable: Identifiable[Event] = Identifiable("event")

  sealed abstract class Status(override val toString: String)

  /**
    * The different states an event can be in. The state diagram is as follows:
    *
    * {{{
    *   Draft ------+
    *     |         |
    *     | (a)     |
    *     |         |
    *     v         |
    *   Open        | (c)
    *     |         |
    *     | (b)     |
    *     |         |
    *     v         |
    *   Cancelled <-+
    * }}}
    *
    * `Draft` events are only visible to organizers, and new organizers may only be added while the
    * event is a draft. This is because organizers have much more permissive access to event and
    * attendance data, so adding an organizer later in the process can expose personal information
    * in ways attendees may not have consented to.
    *
    * While an event is in the `Draft` state, any organizer can move it into the `Open`
    * state (`(a)` in the diagram above) or cancel it (`(c)`). Open events may be cancelled by any
    * organizer, or automatically by the system if at any point there are zero organizers.
    *
    * Cancelled events may not be reopened, and open events may not be reverted to a draft state.
    */
  object Status extends Enum[Status] {
    case object Draft extends Status("draft")
    case object Open extends Status("open")
    case object Cancelled extends Status("cancelled")

    val all: Set[Status] = Set(
      Draft,
      Open,
      Cancelled,
    )
    override val default: Option[Status] = Some(Draft)
  }

  sealed abstract class Visibility(
    override val toString: String,
    val description: String,
  )

  object Visibility extends Enum[Visibility] {
    case object Invite extends Visibility(
      "invite",
      description = "Only people who have been invited.",
    )
    case object Group extends Visibility(
      "group",
      description = "Only members of this event's group(s).",
    )
    case object Link extends Visibility(
      "link",
      description = "Anyone with the link.",
    )

    val all: Set[Visibility] = Set(
      Invite,
      Group,
      Link,
    )
    override val default: Option[Visibility] = Some(Invite)
  }

  sealed abstract class Joinability(
    override val toString: String,
    val description: String,
  )

  object Joinability extends Enum[Joinability] {
    case object InviteOnly extends Joinability(
      "invite_only",
      description = "Only people who have been invited.",
    )
    case object ViewersWithApproval extends Joinability(
      "viewers_with_approval",
      description = "Anyone who can see the event page, but only with organizer approval.",
    )
    case object Viewers extends Joinability(
      "viewers",
      description = "Anyone who can see the event page.",
    )

    val all: Set[Joinability] = Set(
      InviteOnly,
      ViewersWithApproval,
      Viewers,
    )
    override val default: Option[Joinability] = Some(InviteOnly)
  }

  sealed abstract class Postability(
    override val toString: String,
    val description: String,
  )

  object Postability extends Enum[Postability] {
    case object Organizers extends Postability(
      "organizers",
      description = "Only event organizers.",
    )
    case object Attendees extends Postability(
      "attendees",
      description = "Only people marked as attending or waitlisted.",
    )
    case object Invitees extends Postability(
      "invitees",
      description = "Only people who have been invited.",
    )
    case object Viewers extends Postability(
      "viewers",
      description = "Anyone who can see the event page.",
    )

    val all: Set[Postability] = Set(
      Organizers,
      Attendees,
      Invitees,
      Viewers,
    )
    override val default: Option[Postability] = Some(Organizers)
  }
}
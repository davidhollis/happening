package data.model

import java.net.URL
import java.time.ZonedDateTime

import data.{ Enum, HasID, ID, Identifiable }

/**
  * A Post is a message that a [[User]] sends to an [[Event]] page.
  *
  * Posts contain text (markdown-formatted) and may optionally also contain an image and a poll.
  * The Event has controls for determining who can make a Post, and the Post model allows for its
  * author to determine who can see (`visibility`) and comment (`commentability`) on it.
  *
  * In order to make a Post, its author must associate a [[Profile]] with the Event (if they don't
  * have one, a [[Response]] with a status of [[Response.Status.Noncommittal]] will be created). If
  * a viewer does not have permission to see that Profile, they will see an anonymized avatar
  * that's unique to this event, and may be different for other events.
  */
case class Post(
  event: ID[Event],
  author: ID[User],
  visibility: Post.Visibility,
  commentability: Post.Commentability,
  body: String,
  imageURL: Option[URL],
  pollOptions: Seq[String],
  pollMaxSelections: Int,
  pinned: Boolean,
  created: ZonedDateTime,
  updated: ZonedDateTime,
  id: ID[Post] = ID.random,
) extends HasID[Post]

object Post {
  implicit val identifiable: Identifiable[Post] = Identifiable("post")

  sealed abstract class Visibility(
    override val toString: String,
    val description: String,
  )

  object Visibility extends Enum[Visibility] {
    case object OrganizersOnly extends Visibility(
      "organizers_only",
      description = "Only event organizers and people mentioned in the post.",
    )
    case object AttendeesOnly extends Visibility(
      "attendees_only",
      description = "Only people attending the event or mentioned in the post.",
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

  sealed abstract class Commentability(
    override val toString: String,
    val description: String,
  )

  object Commentability extends Enum[Commentability] {
    case object OrganizersOnly extends Commentability(
      "organizers_only",
      description = "Only you, the event organizers and people mentioned in the post.",
    )
    case object AttendeesOnly extends Commentability(
      "attendees_only",
      description = "Only people attending the event or mentioned in the post."
    )
    case object PostersOnly extends Commentability(
      "posters_only",
      description = "Anyone who can post on the event page.",
    )

    val all: Set[Commentability] = Set(
      OrganizersOnly,
      PostersOnly,
    )
    override val default: Option[Commentability] = Some(OrganizersOnly)
  }
}

/**
  * A [[User]]'s response to a poll embedded in a [[Post]].
  *
  * This is visible to anyone who can see the Post, but may be unattributed if the viewer doesn't
  * have permission to see the responder's [[Profile]]. In that case it will be assigned an avatar
  * that's unique within the Event, but may be different on another Event.
  *
  * The indicated [[Event]] must be the same as the indicated [[Post]]'s Event. If they don't
  * match, this Response will not be reported in the results of any Post.
  */
case class PollResponse(
  event: ID[Event],
  poll: ID[Post],
  user: ID[User],
  responses: Seq[String],
)

/**
  * A [[User]]'s emoji reaction to a [[Post]] or [[Comment]].
  *
  * This is visible to anyone who can see the Post, but will always be presented as an anonymized
  * count.
  *
  * The indicated [[Event]] must be the same as the indicated [[Post]] or [[Comment]]'s Event. If
  * they don't match, this Reaction will not be reported anywhere.
  */
case class Reaction(
  event: ID[Event],
  /** A [[Post]] or [[Comment]] id */
  reactionTo: String,
  user: ID[User],
  emoji: String,
)

/**
  * A [[User]]'s comment on a [[Post]], possibly in reply to another comment.
  *
  * This is visible to anyone who can see the Post, but may be unattributed if the viewer doesn't
  * have permission to see the responder's [[Profile]]. In that case it will be assigned an avatar
  * that's unique within the Event, but may be different on another Event.
  *
  * The indicated [[Event]] must be the same as the indicated [[Post]] or [[Comment]]'s Event. If
  * they don't match, this Comment will not be visible anywhere.
  */
case class Comment(
  event: ID[Event],
  replyTo: String,
  author: ID[User],
  body: String,
  created: ZonedDateTime,
  updated: ZonedDateTime,
  id: ID[Comment] = ID.random,
) extends HasID[Comment]

object Comment {
  implicit val identifiable: Identifiable[Comment] = Identifiable("comment")
}
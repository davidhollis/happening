package data.model

import java.time.{ LocalDateTime, ZonedDateTime, ZoneOffset }

import data.{ Enum, HasID, ID, Identifiable }
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
  * A ContactMethod is a way of contacting a user. The pair (type, address) is unique
  * system-wide (i.e., any particular email address or phone number can belong to at
  * most one [[User]]).
  *
  * Contact methods are more private than most fields. Only the User they belong to may
  * see any properties of them, and even then `address` is masked. The full value of
  * `address` is never visible in any frontend-accessible form.
  *
  * If an event invitation would be sent to an email or phone number that currently does
  * not exist, a blank User (a User with no profile, minimal preferences, an only the
  * contact method in question) is created. The contact method created this was will be
  * unverified, but a message will still be sent to it with the invitation.
  *
  * A user may log in via a magic link sent to any of the contact methods they own. A
  * login attempt made to a nonexistent contact method will result in the creation of a
  * blank user account and a dual-purpose verification/login message will be sent to that
  * address. Any repeat login attempts to the same contact method will display a message
  * indicating that a link has already been sent, and the system must wait 24 hours before
  * resending. At most 3 login links via email or 1 login link via text may be sent this
  * way.
  *
  * When a user adds a new contact method to an existing account, a verification message
  * will be sent to that address.
  *
  * Except as specified above, messages are NEVER sent to an unverified contact method.
  * This means that a person who has not ever logged into the site will receive at most
  * one event invite by email/text, and if they ignore it, no further messages can be sent.
  *
  * If an event invitation would be sent to an email or phone number that _does_ exist,
  * that message is sent to every verified, preferred contact method of the user that owns
  * the one being notified. This does mean that if (for example) someone invites
  * david@hollis.computer to an event, the notification may end up being sent to a
  * completely different email or phone number if its owner has configured their account
  * that way.
  *
  */
case class ContactMethod(
  user: ID[User],
  methodType: ContactMethod.Type,
  address: String,
  preferred: Boolean,
  verified: Boolean,
  lastContact: Option[ZonedDateTime],
  failedVerificationAttempts: Int = 0,
  id: ID[ContactMethod] = ID.random,
) extends HasID[ContactMethod] {
  def canSendLoginMessage(at: ZonedDateTime): Boolean =
    verified || {
      val notTooManyAttempts = (failedVerificationAttempts < methodType.maxVerificationMessages);
      val twentyFourHoursAfterLastContact =
        lastContact
          .getOrElse(ZonedDateTime.of(LocalDateTime.MIN, ZoneOffset.UTC))
          .plusHours(24L)
      val notTooRecently = at.isAfter(twentyFourHoursAfterLastContact);

      notTooManyAttempts && notTooRecently
    }
  
  def canSendInvitation: Boolean =
    verified || lastContact.isEmpty
}

object ContactMethod {
  implicit val identifiable: Identifiable[ContactMethod] = Identifiable("contact_method")

  sealed abstract class Type(
    val name: String,
    val maxVerificationMessages: Int,
  ) {
    override def toString: String = name
  }

  object Type extends Enum[Type] {
    case object Email extends Type(
      name = "email",
      maxVerificationMessages = 3,
    )
    case object Phone extends Type(
      name = "phone",
      maxVerificationMessages = 1,
    )

    lazy val all: Set[Type] = Set(
      Email,
      Phone,
    )
  }
}
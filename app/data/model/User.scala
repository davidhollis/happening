package data.model

import data.{ HasID, ID, Identifiable }

/**
  * A User is an account for a single person who interacts with the site. A User may be host
  * Events, own Events, or both.
  *
  * A User has one or more [[ContactMethod]]s and zero or more [[Profile]]s. A person who ends up
  * with two or more accounts may merge them. In this case, the contact methods and profiles of the
  * new User are the unions of the respective sets or the prior two, and the preferences of the new
  * User are the most conservative of the two (i.e., if one User has opted in to a type of
  * notification and the other has not, the new User will not have opted in).
  *
  * All properties of the User and their contact methods are private to only that user. Profile
  * data is private unless explicitly shared (e.g., through RSVPing to an Event).
  */
case class User(id: ID[User] = ID.random) extends HasID[User]

object User {
  implicit val identifiable: Identifiable[User] = Identifiable("user")
}
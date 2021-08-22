package data.persistence

import com.google.inject.Inject

import data.ID
import data.model.User
import util.Environment

class UserQueries @Inject() (
  implicit
  protected val env: Environment,
) {
  import env.dbProfile.api._

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[ID[User]]("id", O.PrimaryKey)
    def * = (id).mapTo[User]
  }
  val all = TableQuery[UsersTable]
  def byId(id: ID[User]) = all.filter(_.id === id)
  def insert(user: User) = (all += user)
  def update(user: User) = byId(user.id).update(user)
  def delete(user: User) = byId(user.id).delete
}
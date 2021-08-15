package data

trait Enum[T] {
  def all: Set[T]

  def default: Option[T] = None

  lazy val byName: Map[String, T] = all.map(el => (el.toString -> el)).toMap

  def get(s: String): Option[T] = byName.get(s).orElse(default)

  def getOrElse[U >: T](s: String, dft: => U): U = byName.getOrElse(s, dft)

  def apply(s: String): T = get(s).get
}
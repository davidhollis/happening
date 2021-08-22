package data

import java.util.UUID
import scala.util.Try

import util.Environment

case class Identifiable[T](prefix: String)

class ID[T](val uuid: UUID, val idable: Identifiable[T]) {
    override lazy val toString: String = s"id:${idable.prefix}:${uuid.toString}"
}

object ID {
    def parse[T](s: String)(implicit idable: Identifiable[T]): Option[ID[T]] =
        s.split(":") match {
            case Array("id", pfx, uuidStr) if pfx == idable.prefix =>
                Try(UUID.fromString(uuidStr)).toOption.map(ID.apply(_))
            case _ => None
        }
    
    def apply[T: Identifiable](uuid: UUID): ID[T] =
        new ID(uuid, implicitly[Identifiable[T]])
    
    def random[T: Identifiable]: ID[T] =
        new ID(UUID.randomUUID, implicitly[Identifiable[T]])
    
    implicit def idColumnType[T: Identifiable](implicit env: Environment) = {
        import env.dbProfile.api._
        
        MappedColumnType.base[ID[T], String](
            { id => id.toString },
            { str => ID.parse[T](str).get },
        )
    }
}

trait HasID[T] { self: T =>
    def id: ID[T]
}

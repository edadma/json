package io.github.edadma.json

object Array {

  def apply(elems: collection.Seq[Any]) = new Array(elems.toVector)

  def apply() = new Array()

}

class Array private[json] (vector: Vector[Any]) extends Seq[Any] with Aggregate {

  def this() = this(Vector())

  private[json] var _parent: Any = _

  vector foreach {
    case o: Object => o._parent = this
    case a: Array  => a._parent = this
    case _         =>
  }

  def parent: Any = _parent

  def apply(i: Int): Any = vector.apply(i)

  def length: Int = vector.length

  def iterator: Iterator[Any] = vector.iterator

  override def toString: String = vector map render mkString ("[", ", ", "]")

}

package io.github.edadma.json

import scala.collection.mutable.ListBuffer

object Array {

  def apply(elems: ListBuffer[Any]) = new Array(elems)

  def apply(elems: Any*) = new Array(new ListBuffer[Any] ++ elems)

}

class Array private[json] (buf: ListBuffer[Any]) extends Seq[Any] with Aggregate {

  def this() = this(new ListBuffer[Any])

  private[json] var _parent: Any = _

  private val vector = buf.toVector

  buf foreach {
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

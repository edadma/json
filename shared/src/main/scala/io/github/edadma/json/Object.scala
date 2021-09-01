package io.github.edadma.json

object Object {
  def apply(elems: (String, Any)*) = new Object(elems.toMap)
}

class Object private (val m: Map[String, Any]) extends Map[String, Any] with Aggregate {
  def this() = this(Map())

  private[json] var _parent: Any = _

  for (v <- m.values)
    v match {
      case o: Object => o._parent = this
      case a: Array  => a._parent = this
      case _         =>
    }

  def parent: Any = _parent

  def get(key: String): Option[Any] = m get key

  def iterator: Iterator[(String, Any)] = m.iterator

  def removed(key: String): Map[String, Any] = m removed key

  def updated[V1 >: Any](key: String, value: V1): Object = new Object(m.updated(key, value))

  override def +[V1 >: Any](kv: (String, V1)): Object = new Object(m + kv)

  def getObj(key: String): Object = m(key).asInstanceOf[Object]

  def getBoolean(key: String): Boolean = m(key).asInstanceOf[Boolean]

  def getDouble(key: String): Double = m(key).asInstanceOf[Number].doubleValue

  def getInt(key: String): Int = m(key).asInstanceOf[Int]

  def getBigInt(key: String): BigInt = m(key).asInstanceOf[BigInt]

  def getString(key: String): String = m(key).asInstanceOf[String]

  def getArray[T](key: String): Array = m(key).asInstanceOf[Array]

  def getBooleanArray(key: String): Seq[Boolean] = m(key).asInstanceOf[List[Boolean]]

  def getDoubleArray(key: String): Seq[Any] = getArray[Double](key)

  def getIntArray(key: String): Seq[Any] = getArray[Int](key)

  def getStringArray(key: String): Seq[Any] = getArray[String](key)

  def getBigIntArray(key: String): Seq[Any] = getArray[BigInt](key)

  override def toString: String = m map { case (k, v) => s"${render(k)}: ${render(v)}" } mkString ("{", ", ", "}")

}

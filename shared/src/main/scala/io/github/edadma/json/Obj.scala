package io.github.edadma.json

object Obj {
  def apply(elems: (String, Any)*) = new Obj(Map(elems: _*))
}

class Obj private (val m: Map[String, Any]) extends Map[String, Any] {
  def this() = this(Map())

  private[json] var _parent: Obj = _

  def parent: Obj = _parent

  def get(key: String): Option[Any] = m get key

  def iterator: Iterator[(String, Any)] = m.iterator

  def removed(key: String): Map[String, Any] = m removed key

  def updated[V1 >: Any](key: String, value: V1): Obj = new Obj(m.updated(key, value))

  override def +[V1 >: Any](kv: (String, V1)) = new Obj(m + kv)

  def getObj(key: String): Obj = m(key).asInstanceOf[Obj]

  def getBoolean(key: String): Boolean = m(key).asInstanceOf[Boolean]

  def getDouble(key: String): Double = m(key).asInstanceOf[Number].doubleValue

  def getInt(key: String): Int = m(key).asInstanceOf[Int]

  def getBigInt(key: String): BigInt = m(key).asInstanceOf[BigInt]

  def getString(key: String): String = m(key).asInstanceOf[String]

  def getList[T](key: String): Seq[Any] = m(key).asInstanceOf[List[T]]

  def getBooleanList(key: String): Seq[Boolean] = m(key).asInstanceOf[List[Boolean]]

  def getDoubleList(key: String): Seq[Any] = getList[Double](key)

  def getIntList(key: String): Seq[Any] = getList[Int](key)

  def getStringList(key: String): Seq[Any] = getList[String](key)

  def getBigIntList(key: String): Seq[Any] = getList[BigInt](key)

  override def toString: String = m mkString ("{", ",", "}")
}

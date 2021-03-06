package xyz.hyperreal.json

import collection.mutable.ListBuffer
import xyz.hyperreal.char_reader.CharReader

object DecimalJSONReader {
  private val reader = new JSONReader(bigDecs = true)

  def fromString(s: String) = reader.fromString(s)

  def fromReader(r: CharReader) = reader.fromReader(r)

  def fromFile(s: String) = reader.fromFile(s)
}

object DefaultJSONReader {
  private val default = new JSONReader(ints = true)

  def fromString(s: String) = default.fromString(s)

  def fromReader(r: CharReader) = default.fromReader(r)

  def fromFile(s: String) = default.fromFile(s)
}

class JSON(val m: Map[String, Any]) extends Map[String, Any] {

//	def apply( key: String ) = m.apply( key )

  def get(key: String) = m get key

  def iterator = m.iterator

  def removed(key: String) = m removed key

  def updated[V1 >: Any](key: String, value: V1) = m.updated(key, value)

  //override def +[V1 >: Any]( kv: (String, V1) ) = m + kv

  //def -( key: String ) = m - key

  def getMap(key: String) = m(key).asInstanceOf[JSON]

  def getBoolean(key: String) = m(key).asInstanceOf[Boolean]

  def getDouble(key: String) = m(key).asInstanceOf[Number].doubleValue

  def getInt(key: String) = m(key).asInstanceOf[Int]

  def getBigInt(key: String) = m(key).asInstanceOf[BigInt]

  def getString(key: String) = m(key).asInstanceOf[String]

  def getList[T](key: String) = m(key).asInstanceOf[List[T]]

  def getBooleanList(key: String) = m(key).asInstanceOf[List[Boolean]]

  def getDoubleList(key: String) = getList[Double](key)

  def getIntList(key: String) = getList[Int](key)

  def getStringList(key: String) = getList[String](key)

  def getBigIntList(key: String) = getList[BigInt](key)

  override def toString = m mkString ("{", ",", "}")
}

class JSONReader(ints: Boolean = false,
                 bigInts: Boolean = false,
                 bigDecs: Boolean = false) {

  def fromString(s: String): Any = fromReader(CharReader.fromString(s))

  def fromReader(r: CharReader) = {
    val (rest, obj) = value(space(r))
    val r1 = skipSpace(rest)

    if (!r1.eoi) error("expected end of input", r1)

    obj
  }

  def fromFile(s: String) = fromReader(CharReader.fromFile(s))

  def error(msg: String, r: CharReader): Nothing = r.error(msg)

  def space(r: CharReader, e: String = "unexpected end of input"): CharReader =
    skipSpace(r) match {
      case rest if rest.eoi => error(e, r)
      case rest             => rest
    }

  def skipSpace(r: CharReader): CharReader =
    if (!r.eoi && r.ch.isWhitespace)
      skipSpace(r.next)
    else
      r

  def dictionary(r: CharReader): (CharReader, JSON) =
    if (r.ch == '{') {
      val r1 = space(r.next)

      if (r1.ch == '}')
        (r1.next, new JSON(Map[String, Any]()))
      else
        members(r1, Map[String, Any]())
    } else
      error("expected '{'", r)

  def members(r: CharReader, m: Map[String, Any]): (CharReader, JSON) = {
    val (r1, map) = pair(r, m)
    val r2 = space(r1, "',' or '}' was expected")

    r2.ch match {
      case ',' => members(space(r2.next, "string was expected"), map)
      case '}' => (r2.next, new JSON(map))
      case _   => error("expected ',' or '}'", r2)
    }
  }

  def pair(r: CharReader,
           m: Map[String, Any]): (CharReader, Map[String, Any]) = {
    val (r1, k) =
      if (r.ch == '"')
        string(r)
      else
        ident(r)

    val (r2, v) = value(
      space(matches(space(r1, "':' was expected"), ":"),
            "JSON value was expected"))

    (r2, m + (k -> v))
  }

  def array(r: CharReader): (CharReader, List[Any]) =
    if (r.ch == ']')
      (r.next, Nil)
    else
      elements(r, new ListBuffer[Any])

  def elements(r: CharReader, buf: ListBuffer[Any]): (CharReader, List[Any]) = {
    val (r1, v) = value(space(r))
    val r2 = space(r1, "',' or ']' was expected")

    buf += v

    r2.ch match {
      case ',' => elements(space(r2.next), buf)
      case ']' => (r2.next, buf.toList)
      case _   => error("expected ',' or ']'", r2)
    }
  }

  def value(r: CharReader): (CharReader, Any) =
    r.ch match {
      case '['                        => array(space(r.next))
      case '{'                        => dictionary(r)
      case '"'                        => string(r)
      case f if f.isDigit || f == '-' => number(r)
      case 't'                        => (matches(r, "true"), true)
      case 'f'                        => (matches(r, "false"), false)
      case 'n'                        => (matches(r, "null"), null)
      case _                          => error("failed to match JSON value", r)
    }

  private val NUMBER =
    """-?(?:0|[1-9]\d*)(?:\.\d*)?(?:[eE](?:\+|-|)\d+)?""".r.pattern
  private val IDENT_CHAR =
    ('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z') :+ '_' :+ '-' toSet

  def ident(r: CharReader): (CharReader, String) = {
    val buf = new StringBuilder

    def read(r: CharReader): CharReader =
      if (!r.eoi && IDENT_CHAR(r.ch)) {
        buf append r.ch
        read(r.next)
      } else
        r

    (read(r), buf.toString)
  }

  def number(r: CharReader): (CharReader, Number) = {
    val buf = new StringBuilder

    def read(r: CharReader): CharReader =
      if (!r.eoi && "+-0123456789eE.".indexOf(r.ch) > -1) {
        buf append r.ch
        read(r.next)
      } else
        r

    val r1 = read(r)
    val n = buf.toString

    if (!NUMBER.matcher(n).matches) error("invalid number", r)

    if (n.indexOf('.') > -1 || n.indexOf('e') > -1 || n.indexOf('E') > -1)
      (r1, if (bigDecs) BigDecimal(n) else n.toDouble)
    else {
      val num = BigInt(n)

      if (ints && num.isValidInt)
        (r1, num.toInt)
      else if (bigInts)
        (r1, num)
      else
        (r1, if (bigDecs) BigDecimal(n) else n.toDouble)
    }
  }

  def string(r: CharReader): (CharReader, String) =
    if (r.ch != '"')
      error("expected '\"'", r)
    else {
      val buf = new StringBuilder

      def read(r: CharReader): CharReader =
        if (r.eoi)
          error("unclosed string", r)
        else if (r.ch == '\\')
          if (r.next.eoi)
            error("unexpected end of input after escape character", r.next)
          else if (r.next.ch == 'u') {
            var r1 = r.next.next
            val ch = new Array[Char](4)

            for (i <- 0 until 4)
              if (r1.eoi)
                error("unexpected end of input within character code", r.next)
              else {
                val c = Character.toLowerCase(r1.ch)

                if ("0123456789abcdef".indexOf(c) == -1)
                  error("invalid character code", r1)

                ch(i) = c
                r1 = r1.next
              }

            buf += Integer.parseInt(ch mkString "", 16).toChar
            read(r1)
          } else {
            buf +=
              (r.next.ch match {
                case '"'  => '"'
                case '\\' => '\\'
                case '/'  => '/'
                case 'b'  => '\b'
                case 'f'  => '\f'
                case 'n'  => '\n'
                case 'r'  => '\r'
                case 't'  => '\t'
                case c    => error("illegal escape character '" + c + "'", r.next)
              })

            read(r.next.next)
          } else if (r.ch == '"')
          r.next
        else {
          buf += r.ch
          read(r.next)
        }

      (read(r.next), buf.toString)
    }

  def matches(r: CharReader, s: String): CharReader = {
    val len = s.length

    def matches(_r: CharReader, index: Int): CharReader =
      if (index == len)
        _r
      else if (!_r.eoi && _r.ch == s.charAt(index))
        matches(_r.next, index + 1)
      else
        error("failed to match '" + s, _r)

    matches(r, 0)
  }
}

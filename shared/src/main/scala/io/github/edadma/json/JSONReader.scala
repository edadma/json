package io.github.edadma.json

import collection.mutable.ListBuffer
import io.github.edadma.char_reader.CharReader

import scala.annotation.tailrec
import scala.language.postfixOps

object DecimalJSONReader {
  private val reader = new JSONReader(bigDecs = true)

  def fromString(s: String): Any = reader.fromString(s)

  def fromReader(r: CharReader): Any = reader.fromReader(r)

  def fromFile(s: String): Any = reader.fromFile(s)
}

object DefaultJSONReader {
  private[json] val reader = new JSONReader()

  def fromString(s: String): Any = reader.fromString(s)

  def fromReader(r: CharReader): Any = reader.fromReader(r)

  def fromFile(s: String): Any = reader.fromFile(s)
}

class JSONReader(
    ints: Boolean = false,
    bigInts: Boolean = false,
    bigDecs: Boolean = false
) {

  def fromString(s: String): Any = fromReader(CharReader.fromString(s))

  def fromReader(r: CharReader): Any = {
    val (rest, obj) = value(space(r))
    val r1          = skipSpace(rest)

    if (!r1.eoi) r1.error("expected end of input")

    obj
  }

  def fromFile(s: String): Any = fromReader(CharReader.fromFile(s))

  def space(r: CharReader, e: String = "unexpected end of input"): CharReader =
    skipSpace(r) match {
      case rest if rest.eoi => r.error(e)
      case rest             => rest
    }

  def skipSpace(r: CharReader): CharReader =
    if (!r.eoi && r.ch.isWhitespace)
      skipSpace(r.next)
    else
      r

  def dictionary(r: CharReader): (CharReader, Object) =
    if (r.ch == '{') {
      val r1 = space(r.next)

      if (r1.ch == '}')
        (r1.next, new Object)
      else
        members(r1, new Object)
    } else
      r.error("expected '{'")

  def members(r: CharReader, obj: Object): (CharReader, Object) = {
    val (r1, newobj) = pair(r, obj)
    val r2           = space(r1, "',' or '}' was expected")

    r2.ch match {
      case ',' => members(space(r2.next, "string was expected"), newobj)
      case '}' => (r2.next, newobj)
      case _   => r2.error("expected ',' or '}'")
    }
  }

  def pair(r: CharReader, obj: Object): (CharReader, Object) = {
    val (r1, k) =
      if (r.ch == '"')
        string(r)
      else
        ident(r)

    val (r2, v) = value(
      space(
        matches(space(r1, "':' was expected"), ":"),
        "JSON value was expected"
      )
    )

    (r2, obj + (k -> v))
  }

  def array(r: CharReader): (CharReader, Array) =
    if (r.ch == ']')
      (r.next, Array())
    else
      elements(r, new ListBuffer[Any])

  def elements(r: CharReader, buf: ListBuffer[Any]): (CharReader, Array) = {
    val (r1, v) = value(space(r))
    val r2      = space(r1, "',' or ']' was expected")

    buf += v

    r2.ch match {
      case ',' => elements(space(r2.next), buf)
      case ']' => (r2.next, Array(buf))
      case _   => r2.error("expected ',' or ']'")
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
      case _                          => r.error("failed to match JSON value")
    }

  private val NUMBER     = """-?(?:0|[1-9]\d*)(?:\.\d*)?(?:[eE](?:\+|-|)\d+)?""".r
  private val IDENT_CHAR = ('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z') :+ '_' :+ '-' toSet

  def ident(r: CharReader): (CharReader, String) = {
    val buf = new StringBuilder

    @tailrec
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

    @tailrec
    def read(r: CharReader): CharReader =
      if (!r.eoi && "+-0123456789eE.".indexOf(r.ch) > -1) {
        buf append r.ch
        read(r.next)
      } else
        r

    val r1 = read(r)
    val n  = buf.toString

    if (!NUMBER.matches(n)) r.error("invalid number")

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
      r.error("expected '\"'")
    else {
      val buf = new StringBuilder

      def read(r: CharReader): CharReader =
        if (r.eoi)
          r.error("unclosed string")
        else if (r.ch == '\\')
          if (r.next.eoi)
            r.next.error("unexpected end of input after escape character")
          else if (r.next.ch == 'u') {
            var r1 = r.next.next
            val ch = new scala.Array[Char](4)

            for (i <- 0 until 4)
              if (r1.eoi)
                r.next.error("unexpected end of input within character code")
              else {
                val c = Character.toLowerCase(r1.ch)

                if ("0123456789abcdef".indexOf(c) == -1)
                  r1.error("invalid character code")

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
                case c    => r.error("illegal escape character '" + c + "'")
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

    @tailrec
    def matches(r: CharReader, index: Int): CharReader =
      if (index == len)
        r
      else if (!r.eoi && r.ch == s.charAt(index))
        matches(r.next, index + 1)
      else
        r.error(s"failed to match '$s'")

    matches(r, 0)
  }

}

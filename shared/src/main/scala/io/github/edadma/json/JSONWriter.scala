package io.github.edadma.json

import java.io.{ByteArrayOutputStream, File, FileOutputStream, OutputStream, PrintStream}
import collection.Map
import scala.annotation.tailrec

object DefaultJSONWriter {
  private val default = new JSONWriter(2)

  def toString(v: Any): String = default.toString(v)

  def write(v: Any, file: File): Unit = default.write(v, file)

  def write(v: Any, file: String): Unit = default.write(v, file)

  def write(v: Any, out: PrintStream): Unit = default.write(v, out)

  def write(v: Any): Unit = default.write(v)
}

class JSONWriter(indent: Int) {
  def toString(v: Any): String = {
    val bytes = new ByteArrayOutputStream

    write(v, bytes)
    new String(bytes.toByteArray, "UTF-8")
  }

  def write(v: Any, file: File): Unit = {
    val out = new FileOutputStream(file)

    write(v, out)
    out.close()
  }

  def write(v: Any, file: String): Unit = {
    val out = new FileOutputStream(file)

    write(v, out)
    out.close()
  }

  def write(v: Any): Unit = write(v, Console.out, nl = true)

  def write(v: Any, out: OutputStream, nl: Boolean = false): Unit = {
    Console.withOut(new PrintStream(out, true, "UTF-8")) {
      def scope(level: Int): Unit = print(" " * (level * indent))

      def writeValue(level: Int, v: Any): Unit =
        v match {
          case s: String => writeString(s)
          case o: Object => writeObject(level, o)
          case a: Array =>
            val l = a.toList

            if (l.isEmpty)
              print("[]")
            else {
              println('[')

              @tailrec
              def members(l: List[Any]): Unit =
                l match {
                  case Nil =>
                  case e :: Nil =>
                    scope(level + 1)
                    writeValue(level + 1, e)
                    println()
                  case e :: tail =>
                    scope(level + 1)
                    writeValue(level + 1, e)
                    println(',')
                    members(tail)
                }

              members(l)
              scope(level)
              print(']')
            }
          case _ => print(v)
        }

      def writeString(s: String): Unit = {
        print('"')

        for (ch <- s)
          escaped.get(ch) match {
            case None    => print(ch)
            case Some(e) => print(e)
          }

        print('"')
      }

      def writeObject(level: Int, o: Object): Unit = {
        if (o.isEmpty)
          print("{}")
        else {
          def pair(k: String, v: Any): Unit = {
            scope(level + 1)
            writeString(k)
            print(": ")
            writeValue(level + 1, v)
          }

          @tailrec
          def pairs(l: List[(String, Any)]): Unit = {
            l match {
              case Nil           =>
              case (k, v) :: Nil => pair(k, v)
              case (k, v) :: tail =>
                pair(k, v)
                println(",")
                pairs(tail)
            }
          }

          println("{")
          pairs(o.toList)
          println()
          scope(level)
          print("}")
        }
      }

      writeValue(0, v)

      if (nl)
        println()
    }
  }

  private val escaped = Map('\\' -> "\\\\",
                            '"'  -> "\\\"",
                            '\t' -> "\\t",
                            '\b' -> "\\b",
                            '\f' -> "\\f",
                            '\n' -> "\\n",
                            '\r' -> "\\r",
                            '\b' -> "\\b")
}

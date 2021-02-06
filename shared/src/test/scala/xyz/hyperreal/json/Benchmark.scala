package xyz.hyperreal.json

import java.io.{File, PrintWriter}
import scala.io.Source

object Benchmark /*extends App*/ {
  var _start: Long = _

  def start(msg: String) = {
    print(msg)
    System.gc
    System.gc
    _start = System.currentTimeMillis
  }

  def time = {
    val res = (System.currentTimeMillis - _start).toDouble

    println(s"${System.currentTimeMillis - _start}ms")
    res
  }

  val RUNS = 3

  def test(msg: String)(activity: => Unit) = {
    var total = 0.0

    for (i <- 0 to RUNS) {
      start(s"$msg ($i)...   ")
      activity

      val t = time

      if (i > 0)
        total += t
    }

    println(s"$msg average: ${(total / RUNS).toInt}ms\n")
  }

  val file = new File("benchmark.txt")

  if (!file.exists) {
    println("Generating test file...")

    val out = new PrintWriter(file)

    for (_ <- 1 to 91582)
      out.println(
        """{"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false, "h": [{"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false}, {"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false}, {"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false}, {"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true,"g":false}],"i":1}""")

    out.close
  }

  test("Base line") {
    util.Using(Source.fromFile(file))(_.getLines()).get.length
  }

  test("JSON reader") {
    util.Using(Source.fromFile(file))(_.getLines()).get foreach DefaultJSONReader.fromString
  }
}

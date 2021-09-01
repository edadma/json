package io.github.edadma.json

object Main extends App {

  val l = DefaultJSONReader.fromString(""" {"a": 3, "b": 4} """)

  println(DefaultJSONWriter.toString(l))

}

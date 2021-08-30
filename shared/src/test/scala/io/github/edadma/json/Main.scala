package io.github.edadma.json

object Main extends App {

  val l = DefaultJSONReader.fromFile("test.txt")

  println(DefaultJSONWriter.toString(l))

}

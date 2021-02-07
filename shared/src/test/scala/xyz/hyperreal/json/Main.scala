package xyz.hyperreal.json

object Main extends App {

  val l = DefaultJSONReader.fromFile("test.txt")

  println(DefaultJSONWriter.toString(l))

}

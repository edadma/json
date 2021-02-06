package xyz.hyperreal.json

object Main extends App {

  val l = DefaultJSONReader.fromString("""{"a": 1.5}""")

  println(l.getClass)
  println(l)
  println(DefaultJSONWriter.toString(l))

}

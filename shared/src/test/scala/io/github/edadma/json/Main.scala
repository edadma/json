package io.github.edadma.json

object Main extends App {

  val json = DefaultJSONReader.fromString("""
      |{
      |  "a": {
      |    "b": 3
      |  },
      |  "c": 4
      |}
      """.stripMargin)

  println(DefaultJSONWriter.toString(json))
  println(json.asInstanceOf[Obj].getObj("a").parent)
}

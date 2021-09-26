package io.github.edadma.json

object Main extends App with Testing {

  val json = """{"a": 123.0}"""

  println(DefaultJSONReader.fromString(json).toString)
  println(test(json))
//  println(DefaultJSONWriter.toString(json))

}

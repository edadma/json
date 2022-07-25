package io.github.edadma.json

object Main extends App with Testing {

  val json = """{"a": 123.0}"""

  println(new JSONWriter(0).toString(DefaultJSONReader.fromString(json)))
  println(test(json))
//  println(DefaultJSONWriter.toString(json))

}

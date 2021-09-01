package io.github.edadma.json

import scala.collection.mutable.ListBuffer

object Main extends App {

  //  val json = DefaultJSONReader.fromString("""
  //      |{
  //      |  "a": {
  //      |    "b": 3
  //      |  },
  //      |  "c": 4
  //      |}
  //      """.stripMargin)
  val json = DefaultJSONReader.fromString("{\"a\": \"Studio\\nTeam \\u2013 First\"}")

  println(json)
  println(DefaultJSONWriter.toString(json))

}

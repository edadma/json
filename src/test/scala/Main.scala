package xyz.hyperreal.json


object Main extends App {

  val l = new JSONReader('ints).fromFile("json").asInstanceOf[List[Map[String, Any]]]

  println( DefaultJSONWriter.toString( l filter (_("section") == "Thematic breaks") ) )

}
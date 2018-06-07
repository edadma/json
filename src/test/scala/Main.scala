package xyz.hyperreal.json


object Main extends App {
	
//	println( DefaultJSONWriter.toString( new JSONReader('ints).fromString(" {\"a\": \"\\u0061\"} " ) ) )
  println( DefaultJSONWriter.toString( new JSONReader('ints).fromFile("json") ) )

}
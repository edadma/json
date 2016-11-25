package xyz.hyperreal.json


object Main extends App {
	
	println( DefaultJSONWriter.toString( new JSONReader('ints).fromString("""{"a": 123}""") ) )
	
}
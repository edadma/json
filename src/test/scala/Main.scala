package xyz.hyperreal.json


object Main extends App {
	
	println( new JSONReader('ints).fromString("""{"a": 123}""") )
	
}
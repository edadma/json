package funl.json


object JSONTest extends App
{
	println( DefaultJSONWriter.toString( DefaultJSONReader.fromFile("test.txt") ) )
}

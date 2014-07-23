package funl.json


object JSONTest extends App
{
	JSONWriter.write( JSONReader.fromFile("test.txt") )
}

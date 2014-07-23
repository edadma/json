package funl.json


object JSONTest extends App
{
	new JSONWriter(2).write( JSONReader.fromFile("test.txt") )
}

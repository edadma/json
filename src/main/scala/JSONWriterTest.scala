package funl.json


object JSONWriterTest extends App
{
	new JSONWriter(2).write( Map("a" -> 1, "map" -> Map("c" -> 3, "d" -> 4), "b" -> 2) )
}

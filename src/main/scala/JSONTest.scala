package funl.json


object JSONTest extends App
{
	println( new JSONReader(ints = true, bigInts = true).fromString("""{"a": 3123123123}""")("a").getClass )
}

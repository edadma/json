package funl.json


object JSONReaderTest extends App
{
	println( JSONReader("""
		{
			"string0": "",
			"string": "asdf",
			"number": 123,
			"array0": [],
			"array1": [1],
			"array2": [1, "two"],
			"object0": {},
			"object1": {"negative": -1},
			"object2": {"one": 1, "two": 2}
		}
		""") )
}

package xyz.hyperreal.json

import java.{lang => jl}

import org.scalatest._
import prop.PropertyChecks


class JSONTest extends FreeSpec with PropertyChecks with Matchers
{
	"reading" in
	{
		new JSONReader( 'ints, 'bigInts ).fromString("""{"a": 3123123123}""").m shouldBe Map( "a" -> BigInt("3123123123") )
		DefaultJSONReader.fromString("""{"a": 1.5}""").m shouldBe Map( "a" -> 1.5 )
		DefaultJSONReader.fromString("""{"a": 123}""").m shouldBe Map( "a" -> 123 )
		DefaultJSONReader.fromString("""{a: 123}""").m shouldBe Map( "a" -> 123 )
		DefaultJSONReader.fromString("""{"a": 123}""")("a") should (be (123) and be (a [jl.Integer]))
		new JSONReader( 'ints ).fromString("""{"a": 123}""")("a") should (be (123) and be (a [jl.Integer]))
		DefaultJSONReader.fromString("""{"a": [1, 2]}""").m shouldBe Map( "a" -> List(1, 2) )
		DefaultJSONReader.fromString(" \n{ \n} \n").m shouldBe Map()
		DefaultJSONReader.fromString("""{"a": null}""").m shouldBe Map( "a" -> null )
		DefaultJSONReader.fromString("""{"a": true}""").m shouldBe Map( "a" -> true )
		DefaultJSONReader.fromString("""{"a": false}""").m shouldBe Map( "a" -> false )
		DefaultJSONReader.fromString("""{"a": [1]}""").getDoubleList( "a" ).head should (be (1) and be (a [jl.Double]))
	}
	
	"writing" in
	{
		DefaultJSONWriter.toString( Map("a" -> List(1, 2)) ) shouldBe
			"""	|{
				|  "a": [
				|    1,
				|    2
				|  ]
				|}""".stripMargin
	}
}

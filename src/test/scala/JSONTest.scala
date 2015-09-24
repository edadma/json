package ca.hyperreal.json

import java.{lang => jl}

import org.scalatest._
import prop.PropertyChecks


class JSONTest extends FreeSpec with PropertyChecks with Matchers
{
	"reading" in
	{
		new JSONReader( Set("ints", "bigInts") ).fromString("""{"a": 3123123123}""") shouldBe Map( "a" -> BigInt("3123123123") )
		DefaultJSONReader.fromString("""{"a": 1.5}""") shouldBe Map( "a" -> 1.5 )
		DefaultJSONReader.fromString("""{"a": 123}""") shouldBe Map( "a" -> 123 )
		DefaultJSONReader.fromString("""{"a": 123}""")("a") should (be (123) and be (a [jl.Double]))
		new JSONReader( Set("ints") ).fromString("""{"a": 123}""")("a") should (be (123) and be (a [jl.Integer]))
		DefaultJSONReader.fromString("""{"a": [1, 2]}""") shouldBe Map( "a" -> List(1, 2) )
		DefaultJSONReader.fromString(" \n{ \n} \n") shouldBe Map()
		DefaultJSONReader.fromString("""{"a": null}""") shouldBe Map( "a" -> null )
		DefaultJSONReader.fromString("""{"a": true}""") shouldBe Map( "a" -> true )
		DefaultJSONReader.fromString("""{"a": false}""") shouldBe Map( "a" -> false )
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

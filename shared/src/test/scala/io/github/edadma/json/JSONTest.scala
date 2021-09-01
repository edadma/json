package io.github.edadma.json

import java.{lang => jl}

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class JSONTest extends AnyFreeSpec with Matchers {
	"reading" in {
		new JSONReader( ints = true, bigInts = true ).fromString("""{"a": 3123123123}""").asInstanceOf[Object].m shouldBe Map( "a" -> BigInt("3123123123") )
		DefaultJSONReader.fromString("""{"a": 1.5}""").asInstanceOf[Object].m shouldBe Map( "a" -> 1.5 )
		DefaultJSONReader.fromString("""{"a": 123}""").asInstanceOf[Object].m shouldBe Map( "a" -> 123 )
		DefaultJSONReader.fromString("""{a: 123}""").asInstanceOf[Object].m shouldBe Map( "a" -> 123 )
		DefaultJSONReader.fromString("""{"a": 123}""").asInstanceOf[Object]("a") should (be (123) and be (a [jl.Integer]))
		new JSONReader( ints = true ).fromString("""{"a": 123}""").asInstanceOf[Object]("a") should (be (123) and be (a [jl.Integer]))
		DefaultJSONReader.fromString("""{"a": [1, 2]}""").asInstanceOf[Object].m shouldBe Map( "a" -> List(1, 2) )
		DefaultJSONReader.fromString(" \n{ \n} \n").asInstanceOf[Object].m shouldBe Map()
		DefaultJSONReader.fromString("""{"a": null}""").asInstanceOf[Object].m shouldBe Map( "a" -> null )
		DefaultJSONReader.fromString("""{"a": true}""").asInstanceOf[Object].m shouldBe Map( "a" -> true )
		DefaultJSONReader.fromString("""{"a": false}""").asInstanceOf[Object].m shouldBe Map( "a" -> false )
		new JSONReader().fromString("""{"a": [1]}""").asInstanceOf[Object].getDoubleList( "a" ).head should (be (1) and be (a [jl.Double]))
    DefaultJSONReader.fromString("{\"a\": \"Studio\\nTeam \\u2013 First\"}").asInstanceOf[Object].m shouldBe Map( "a" -> "Studio\nTeam \u2013 First" )
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

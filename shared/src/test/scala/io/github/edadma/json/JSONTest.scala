package io.github.edadma.json

import java.{lang => jl}

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class JSONTest extends AnyFreeSpec with Matchers with Testing {
  "reading" in {
    assert(test(new JSONReader(ints = true, bigInts = true), """{"a": 3123123123}"""))
    assert(test("""{"a": 1.5}"""))
    assert(test("""{"a": 123.0}"""))
    DefaultJSONReader.fromString("""{a: 123}""").toString shouldBe """{"a": 123.0}"""
    new JSONReader(ints = true).fromString("""{"a": 123}""").asInstanceOf[Object]("a") should (be(123) and be(
      a[jl.Integer]))
    assert(test("""{"a": [1.0, 2.0]}"""))
    DefaultJSONReader.fromString(" \n{ \n} \n").toString shouldBe "{}"
    assert(test("""{"a": null}"""))
    assert(test("""{"a": true}"""))
    assert(test("""{"a": false}"""))
    new JSONReader().fromString("""{"a": [1]}""").asInstanceOf[Object].getDoubleArray("a").head should (be(1) and be(
      a[jl.Double]))
    DefaultJSONWriter.toString(DefaultJSONReader.fromString("{\"a\": \"Studio\\nTeam \\u2013 First\"}")) shouldBe
      """
        |{
        |  "a": "Studio\nTeam – First"
        |}""".trim.stripMargin
  }

  "writing" in {
    DefaultJSONWriter.toString(Object("a" -> Array(List(1, 2)))) shouldBe
      """	|{
        |  "a": [
        |    1,
        |    2
        |  ]
        |}""".stripMargin
  }
}

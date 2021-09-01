package io.github.edadma.json

trait Testing {

  def test(reader: JSONReader, json: String): Boolean = reader.fromString(json).toString == json

  def test(json: String): Boolean = test(DefaultJSONReader.reader, json)

}

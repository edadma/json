package ca.hyperreal.json

import compat.Platform
import io.Source
import java.io.{File, PrintWriter}


object Benchmark extends App {
	var _start: Long = _
	
	def start( msg: String ) = {
		print( msg )
		Platform.collectGarbage
		Platform.collectGarbage
		_start = Platform.currentTime
	}
	
	def time = {
		val res = (Platform.currentTime - _start).toDouble
		
		printf( "%.2fms\n", (Platform.currentTime - _start).toDouble )
		res
	}
	
	val RUNS = 3
	
	def test( msg: String )( activity: => Unit ) {
		var total = 0.0
		
		for (i <- 0 to RUNS) {
			start( s"$msg ($i)... " )
			activity
			
			val t = time
			
			if (i > 0)
				total += t
		}
		
		printf( msg + " average: %.2fms\n\n", total/RUNS )
	}
	
	val file = new File( "benchmark.txt" )
	
	if (!file.exists) {
		println( "Generating test file..." )
		
		val out = new PrintWriter( file )
		
		for (_ <- 1 to 91582)
			out.println( """{"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false, "h": [{"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false}, {"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false}, {"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g": false}, {"a": 123, "b": "asdf", "c": [], "d": {}, "e": null, "f": true, "g":false}],"i":1}""" )
			
		out.close
	}
	
	test( "Base line" ) {
		Source.fromFile( file ).getLines.length
	}
	
	test( "JSON reader" ) {
		Source.fromFile( file ).getLines foreach DefaultJSONReader.fromString
	}
}

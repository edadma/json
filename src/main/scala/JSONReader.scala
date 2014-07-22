package funl.json

import java.io.File
import collection.mutable.ListBuffer
import collection.immutable.PagedSeq
import util.parsing.input.{Reader, CharSequenceReader, PagedSeqReader}


object JSONReader extends App
{
	def fromString( s: String ): Map[String, Any] = fromReader( new CharSequenceReader(s) )
	
	def fromReader( r: Reader[Char] ): Map[String, Any] =
	{
	val (rest, obj) = dictionary( space(r) )
	val r1 = skipSpace( rest )
	
		if (!r1.atEnd) error( "expected end of input", r1 )

		obj
	}

	def fromFile( s: String ) = fromReader( new PagedSeqReader(PagedSeq.fromFile(s)) )

	def fromFile( s: File ) = fromReader( new PagedSeqReader(PagedSeq.fromFile(s)) )
	
	def error( msg: String, r: Reader[Char] ): Nothing = sys.error( msg + " at " + r.pos + "\n" + r.pos.longString )
	
	def space( r: Reader[Char] ): Reader[Char] =
		skipSpace( r ) match
		{
			case rest if rest.atEnd => error( "unexpected end of input", r )
			case rest => rest
		}
	
	def skipSpace( r: Reader[Char] ): Reader[Char] =
		if (!r.atEnd && r.first.isWhitespace)
			skipSpace( r.rest )
		else
			r

	def dictionary( r: Reader[Char] ): (Reader[Char], Map[String, Any]) =
		if (r.first == '{')
		{
		val r1 = space( r.rest )
		
			if (r1.first == '}')
				(r1.rest, Map[String, Any]())
			else
				members( r1, Map[String, Any]() )
		}
		else
			error( "expected '{'", r )
		
	def members( r: Reader[Char], m: Map[String, Any] ): (Reader[Char], Map[String, Any]) =
	{
	val (r1, map) = pair( r, m )
	val r2 = space( r1 )
		
		r2.first match
		{
			case ',' => members( space(r2.rest), map )
			case '}' => (r2.rest, map)
			case _ => error( "expected ',' or '}'", r2 )
		}
	}
	
	def pair( r: Reader[Char], m: Map[String, Any] ): (Reader[Char], Map[String, Any]) =
	{
	val (r1, k) = string( r )
	val (r2, v) = value( space(matches(space(r1), ":")) )
	
		(r2, m + (k -> v))
	}
	
	def array( r: Reader[Char] ): (Reader[Char], List[Any]) =
		if (r.first == ']')
			(r.rest, Nil)
		else
			elements( r, new ListBuffer[Any] )

	def elements( r: Reader[Char], buf: ListBuffer[Any] ): (Reader[Char], List[Any]) =
	{
	val (r1, v) = value( space(r) )
	val r2 = space( r1 )
	
		buf += v
		
		r2.first match
		{
			case ',' => elements( space(r2.rest), buf )
			case ']' => (r2.rest, buf.toList)
			case _ => error( "expected ',' or ']'", r2 )
		}
	}

	def value( r: Reader[Char] ): (Reader[Char], Any) =
		r.first match
		{
			case '[' => array( space(r.rest) )
			case '{' => dictionary( r )
 			case '"' => string( r )
 			case f if f.isDigit || f == '-' => number( r )
			case 't' => (matches( r, "true" ), true)
			case 'f' => (matches( r, "false" ), false)
			case 'n' => (matches( r, "null" ), null)
			case _ => error( "failed to match JSON value", r )
		}

	def number( r: Reader[Char] ): (Reader[Char], Number) =
	{
	val buf = new StringBuilder
	val NUMBER = """-?(?:0|[1-9]\d*)(?:\.\d*)?(?:(?:e|E)(?:\+|-|)\d+)?""".r.pattern
		
		def read( r: Reader[Char] ): Reader[Char] =
			if (!r.atEnd && "+-0123456789eE".indexOf( r.first ) > -1)
			{
				buf append r.first
				read( r.rest )
			}
			else
				r
		
	val r1 = read( r )
	val n = buf.toString
	
		if (!NUMBER.matcher( n ).matches) error( "invalid number", r )
			
		(r1, n.toDouble)
	}

	def string( r: Reader[Char] ): (Reader[Char], String) =
		if (r.first != '"')
			error( "expected '\"'", r )
		else
		{
		val buf = new StringBuilder
		
			def read( r: Reader[Char] ): Reader[Char] =
				if (r.atEnd)
					error( "unclosed string", r )
				else if (r.first == '\\')
					if (r.rest.atEnd)
						error( "unexpected end of input after escape character", r.rest )
					else if (r.rest.first == 'u')
					{
					var r1 = r.rest.rest
					val ch = Array[Char]( 4 )
						
						for (i <- 0 until 4)
							if (r1.atEnd)
								error( "unexpected end of input within character code", r.rest )
							else
							{
							val c = Character.toLowerCase( r1.first )
							
								if ("0123456789abcdef".indexOf( c ) == -1)
									error( "invalid character code", r1 )
									
								ch(i) = c
								r1 = r1.rest
							}
							
						buf += Integer.parseInt( ch mkString "", 16 ).toChar
						read( r1 )
					}
					else
					{
						buf +=
							(r.rest.first match
							{
								case '"' => '"'
								case '\\' => '\\'
								case '/' => '/'
								case 'b' => '\b'
								case 'f' => '\f'
								case 'n' => '\n'
								case 'r' => '\r'
								case 't' => '\t'
								case c => error( "illegal escape character '" + c + "'", r.rest )
							})

						read( r.rest.rest )
					}
				else if (r.first == '"')
					r.rest
				else
				{
					buf += r.first
					read( r.rest )
				}
			
			(read( r.rest ), buf.toString)
		}
	
	def matches( r: Reader[Char], s: String ): Reader[Char] =
	{
		def _matches( _r: Reader[Char], index: Int ): Reader[Char] =
			if (!r.atEnd && r.first ==  s.charAt( index ))
				if (index + 1 == s.length)
					_r.rest
				else
					_matches( _r.rest, index + 1 )
			else
				error( "failed to match '" + s, r )
				
		_matches( r, 0 )
	}
}

package xyz.hyperreal.json

import java.io.File
import collection.mutable.ListBuffer
import util.parsing.input.{PagedSeq, Reader, CharSequenceReader, PagedSeqReader}


object DecimalJSONReader {
  private val reader = new JSONReader( 'bigDecs )

  def fromString( s: String ) = reader.fromString( s )

  def fromReader( r: Reader[Char] ) = reader.fromReader( r )

  def fromFile( s: String ) = reader.fromFile( s )

  def fromFile( s: File ) = reader.fromFile( s )

  def fromSource( s: io.Source ) = reader.fromSource( s )
}

object DefaultJSONReader {
	private val default = new JSONReader( 'ints )
	
	def fromString( s: String ) = default.fromString( s )
	
	def fromReader( r: Reader[Char] ) = default.fromReader( r )

	def fromFile( s: String ) = default.fromFile( s )

	def fromFile( s: File ) = default.fromFile( s )

  def fromSource( s: io.Source ) = default.fromSource( s )
}

class JSON( val m: Map[String, Any] ) extends Map[String, Any] {
	
//	def apply( key: String ) = m.apply( key )
	
	def get( key: String ) = m get key
	
	def iterator = m.iterator
	
	def +[V1 >: Any]( kv: (String, V1) ) = m + kv
	
	def -( key: String ) = m - key
	
	def getMap( key: String ) = m( key ).asInstanceOf[JSON]
	
	def getBoolean( key: String ) = m( key ).asInstanceOf[Boolean]
	
	def getDouble( key: String ) = m( key ).asInstanceOf[Number].doubleValue
	
	def getInt( key: String ) = m( key ).asInstanceOf[Int]

	def getBigInt( key: String ) = m( key ).asInstanceOf[BigInt]
	
	def getString( key: String ) = m( key ).asInstanceOf[String]
	
	def getList[T]( key: String ) = m( key ).asInstanceOf[List[T]]
	
	def getBooleanList( key: String ) = m( key ).asInstanceOf[List[Boolean]]
	
	def getDoubleList( key: String ) = getList[Double]( key )
	
	def getIntList( key: String ) = getList[Int]( key )
	
	def getStringList( key: String ) = getList[String]( key )
	
	def getBigIntList( key: String ) = getList[BigInt]( key )

	override def toString = m mkString ("{", ",", "}")
}

class JSONReader( types: Symbol* ) {
	private val ints = types contains 'ints
	private val bigInts = types contains 'bigInts
	private val bigDecs = types contains 'bigDecs

	def fromString( s: String ) = fromReader( new CharSequenceReader(s) )
	
	def fromReader( r: Reader[Char] ) = {
	val (rest, obj) = value( space(r) )
	val r1 = skipSpace( rest )
	
		if (!r1.atEnd) error( "expected end of input", r1 )

		obj
	}

	def fromSource( s: io.Source ) = fromReader( new PagedSeqReader(PagedSeq.fromSource(s)) )

	def fromFile( s: String ) = fromReader( new PagedSeqReader(PagedSeq.fromFile(s)) )

	def fromFile( s: File ) = fromReader( new PagedSeqReader(PagedSeq.fromFile(s)) )


	def error( msg: String, r: Reader[Char] ): Nothing = sys.error( msg + " at " + r.pos + "\n" + r.pos.longString )
	
	def space( r: Reader[Char], e: String = "unexpected end of input" ): Reader[Char] =
		skipSpace( r ) match {
			case rest if rest.atEnd => error( e, r )
			case rest => rest
		}
	
	def skipSpace( r: Reader[Char] ): Reader[Char] =
		if (!r.atEnd && r.first.isWhitespace)
			skipSpace( r.rest )
		else
			r

	def dictionary( r: Reader[Char] ): (Reader[Char], JSON) =
		if (r.first == '{') {
		  val r1 = space( r.rest )
		
			if (r1.first == '}')
				(r1.rest, new JSON( Map[String, Any]() ))
			else
				members( r1, Map[String, Any]() )
		} else
			error( "expected '{'", r )
		
	def members( r: Reader[Char], m: Map[String, Any] ): (Reader[Char], JSON) = {
  	val (r1, map) = pair( r, m )
	  val r2 = space( r1, "',' or '}' was expected" )
		
		r2.first match {
			case ',' => members( space(r2.rest, "string was expected"), map )
			case '}' => (r2.rest, new JSON( map ))
			case _ => error( "expected ',' or '}'", r2 )
		}
	}
	
	def pair( r: Reader[Char], m: Map[String, Any] ): (Reader[Char], Map[String, Any]) = {
	  val (r1, k) =
      if (r.first == '"')
        string( r )
      else
        ident( r )
			
	  val (r2, v) = value( space(matches(space(r1, "':' was expected"), ":"), "JSON value was expected") )
	
		(r2, m + (k -> v))
	}
	
	def array( r: Reader[Char] ): (Reader[Char], List[Any]) =
		if (r.first == ']')
			(r.rest, Nil)
		else
			elements( r, new ListBuffer[Any] )

	def elements( r: Reader[Char], buf: ListBuffer[Any] ): (Reader[Char], List[Any]) = {
    val (r1, v) = value( space(r) )
    val r2 = space( r1, "',' or ']' was expected" )
	
		buf += v
		
		r2.first match {
			case ',' => elements( space(r2.rest), buf )
			case ']' => (r2.rest, buf.toList)
			case _ => error( "expected ',' or ']'", r2 )
		}
	}

	def value( r: Reader[Char] ): (Reader[Char], Any) =
		r.first match {
			case '[' => array( space(r.rest) )
			case '{' => dictionary( r )
 			case '"' => string( r )
 			case f if f.isDigit || f == '-' => number( r )
			case 't' => (matches( r, "true" ), true)
			case 'f' => (matches( r, "false" ), false)
			case 'n' => (matches( r, "null" ), null)
			case _ => error( "failed to match JSON value", r )
		}

	private val NUMBER = """-?(?:0|[1-9]\d*)(?:\.\d*)?(?:(?:e|E)(?:\+|-|)\d+)?""".r.pattern
	private val IDENT_CHAR = ('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z') :+ '_' :+ '-' toSet
	
	def ident( r: Reader[Char] ): (Reader[Char], String) = {
  	val buf = new StringBuilder
		
		def read( r: Reader[Char] ): Reader[Char] =
			if (!r.atEnd && IDENT_CHAR( r.first )) {
				buf append r.first
				read( r.rest )
			} else
				r
		
		(read( r ), buf.toString)
	}
	
	def number( r: Reader[Char] ): (Reader[Char], Number) = {
	  val buf = new StringBuilder
		
		def read( r: Reader[Char] ): Reader[Char] =
			if (!r.atEnd && "+-0123456789eE.".indexOf( r.first ) > -1) {
				buf append r.first
				read( r.rest )
			} else
				r
		
    val r1 = read( r )
    val n = buf.toString
	
		if (!NUMBER.matcher( n ).matches) error( "invalid number", r )
		
		if (n.indexOf( '.' ) > -1 || n.indexOf( 'e' ) > -1 || n.indexOf( 'E' ) > -1)
			(r1, if (bigDecs) BigDecimal( n ) else n.toDouble)
		else {
	  	val num = BigInt( n )
		
			if (ints && num.isValidInt)
				(r1, num.toInt)
			else if (bigInts)
				(r1, num)
			else
				(r1, if (bigDecs) BigDecimal( n ) else n.toDouble)
		}
	}

	def string( r: Reader[Char] ): (Reader[Char], String) =
		if (r.first != '"')
			error( "expected '\"'", r )
		else {
		  val buf = new StringBuilder
		
			def read( r: Reader[Char] ): Reader[Char] =
				if (r.atEnd)
					error( "unclosed string", r )
				else if (r.first == '\\')
					if (r.rest.atEnd)
						error( "unexpected end of input after escape character", r.rest )
					else if (r.rest.first == 'u') {
				  	var r1 = r.rest.rest
				  	val ch = new Array[Char]( 4 )
						
						for (i <- 0 until 4)
							if (r1.atEnd)
								error( "unexpected end of input within character code", r.rest )
							else {
						  	val c = Character.toLowerCase( r1.first )
							
								if ("0123456789abcdef".indexOf( c ) == -1)
									error( "invalid character code", r1 )

                ch(i) = c
								r1 = r1.rest
							}
							
						buf += Integer.parseInt( ch mkString "", 16 ).toChar
						read( r1 )
					} else {
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
					} else if (r.first == '"')
            r.rest
				else {
					buf += r.first
					read( r.rest )
				}
			
			(read( r.rest ), buf.toString)
		}
	
	def matches( r: Reader[Char], s: String ): Reader[Char] = {
	  val len = s.length
	
		def matches( _r: Reader[Char], index: Int ): Reader[Char] =
			if (index == len)
				_r
			else if (!_r.atEnd && _r.first ==  s.charAt( index ))
				matches( _r.rest, index + 1 )
			else
				error( "failed to match '" + s, _r )
				
		matches( r, 0 )
	}
}

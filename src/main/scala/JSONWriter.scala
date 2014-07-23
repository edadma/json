/*     ______            __                                     *\
**    / ____/_  __ ___  / /     FunL Programming Language       **
**   / __/ / / / / __ \/ /      (c) 2014 Edward A. Maxedon, Sr. **
**  / /   / /_/ / / / / /__     http://funl-lang.org/           **
** /_/    \____/_/ /_/____/                                     **
\*                                                              */

package funl.json

import java.io.{PrintStream, File}

import collection.Map


class JSONWriter( indent: Int )
{
	def write( m: Map[String, Any], file: File )
	{
		write( m, new PrintStream( file ) )
	}
	
	def write( m: Map[String, Any], file: String )
	{
		write( m, new PrintStream( file ) )
	}
	
	def write( m: Map[String, Any], out: PrintStream )
	{
		Console.withOut( out )( write(m) )
		out.close
	}

	private val escaped = Map( '\\' -> "\\\\", '"' -> "\\\"", '\t' -> "\\t", '\b' -> "\\b", '\f' -> "\\f", '\n' -> "\\n", '\r' -> "\\r", '\b' -> "\\b" )
	
	def write( m: Map[String, Any] )
	{
		def scope( level: Int ) = print( " "*(level*indent) )
		
		def writeMap( level: Int, m: Map[String, Any] )
		{
			if (m.isEmpty)
				print( "{}" )
			else
			{
				def writeString( s: String )
				{
					print( '"' )
					
					for (ch <- s)
						escaped.get( ch ) match
						{
							case None => print( ch )
							case Some( e ) => print( e )
						}
					
					print( '"' )
				}
			
				def writeValue( level: Int, v: Any ): Unit =
					v match
					{
						case s: String => writeString( s )
						case m: Map[String, Any] => writeMap( level, m )
						case s: Seq[Any] =>
							val l = s.toList
							
							if (l isEmpty)
								print( "[]" )
							else
							{
								println( '[' )
								
								def members( l: List[Any] ): Unit =
									l match
									{
										case e :: Nil =>
											scope( level + 1 )
											writeValue( level + 1, e )
											println()
										case e :: tail =>
											scope( level + 1 )
											writeValue( level + 1, e )
											println( ',' )
											members( tail )
									}
								
								members( l )
								scope( level )
								print( ']' )
							}
						case _ => print( v )
					}
			
				def pair( k: String, v: Any )
				{
					scope( level + 1 )
					writeString( k )
					print( ": " )
					writeValue( level + 1, v )
				}
				
				def pairs( l: List[(String, Any)] )
				{
					l match
					{
						case (k, v) :: Nil => pair( k, v )
						case (k, v) :: tail =>
							pair( k, v )
							println( "," )
							pairs( tail )
					}
				}
			
				println( "{" )
				pairs( m.toList )
				println()
				scope( level )
				print( "}" )
			}
		}
			
		writeMap( 0, m )
		println()
	}
}

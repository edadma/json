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

	def write( m: Map[String, Any] )
	{
		def writeMap( level: Int, m: Map[String, Any] )
		{
		val scope = " "*(level*indent)
		
			if (m.isEmpty)
				print( "{}" )
			else
			{
				def pairs( l: List[(String, Any)] )
				{
					
				}
			
				println( "{" )
				pairs( m.toList )
				println( "}" )
			}
		}
		
		def writeString( s: String )
		{
			print( '"' )
			
			for (ch <- s)
				
			
			print( '"' )
		}
		
		writeMap( 0, m )
		println()
	}
}

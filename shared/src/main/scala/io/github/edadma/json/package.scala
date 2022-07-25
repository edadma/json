package io.github.edadma

package object json {

  private[json] def render(v: Any) =
    v match {
      case s: String => s"\"$s\""
      case _ => String.valueOf(v)
    }

}

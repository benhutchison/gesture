package gesture

import org.scalajs.dom.PointerEvent

/** We convert from dom.PointerEvent to an internal ADT representation for 3 reasons:
* - Sealed traits are better in match statements than raw PointerEvents which have a String `type` field.
* - PointerEvents are hard to create and set timestamps on in unit tests
* - PointerEvents arent always supported in non-dom environments like Nodejs where we might want to test
* */

sealed trait PointerAdt
case class PointerMove(p: Vec2d, timestamp: Long) extends PointerAdt
case class PointerDown(p: Vec2d, timestamp: Long) extends PointerAdt
case class PointerUp(p: Vec2d, timestamp: Long) extends PointerAdt
case class PointerLeave(p: Vec2d, timestamp: Long) extends PointerAdt

object PointerAdt:
  def fromPointerEvent(p: PointerEvent) =
    def pointerAdt(ctor: (Vec2d, Long) => PointerAdt) = Some(ctor((p.clientX, p.clientY), p.timeStamp.toLong))
    p.`type` match {
      case "pointermove" => pointerAdt(PointerMove.apply)
      case "pointerdown" => pointerAdt(PointerDown.apply)
      case "pointerup" => pointerAdt(PointerUp.apply)
      case "pointerleave" => pointerAdt(PointerLeave.apply)
      case _ => None
    }


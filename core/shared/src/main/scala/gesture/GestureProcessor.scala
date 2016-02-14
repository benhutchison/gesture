package gesture

import cats._
import cats.state._
import cats.std.all._

class GestureProcessor(dragThreshold: Double = 5, ClickDistThreshold: Double = 4.0, ClickTimeThreshold: Double = 400) {

  def pointerDown(pe: PointerDown) = State[PointerState, GestureEvent](ps => ps match {
    case Up() =>
      (Down(pe.p, pe.timestamp), Noop)
    case _ => invalid(pe, ps)
  })

  def invalid(pe: PointerEvent, ps: PointerState) = (ps, Invalid(s"Unexpected input when in state: $ps", pe))

  def pointerUp(pe: PointerUp) = State[PointerState, GestureEvent](ps => ps match {
    case Down(p, timestamp) =>
      val elapsed = pe.timestamp - timestamp
      val dist = pe.p.distanceTo(p)
      val gesture = if (dist < ClickDistThreshold && elapsed < ClickTimeThreshold)
        Click(p, pe.timestamp)
      else
        Noop
      (Up(), gesture)
    case Drag(from, fromTimestamp, to, toTimestamp) =>
      (Up(), DragComplete(from, fromTimestamp, pe.p, pe.timestamp, pe.p - to))
    case _ => invalid(pe, ps)
  })

  def pointerMove(pe: PointerMove) = State[PointerState, GestureEvent](ps => ps match {
    case Up() => (Up(), Noop)
    case Down(p, timestamp) =>
      if (p.distanceTo(pe.p) > dragThreshold)
        (Drag(p, timestamp, pe.p, pe.timestamp), DragStart(p, timestamp, pe.p, pe.timestamp, pe.p - p))
      else
        (ps, Noop)
    case Drag(from, fromTimestamp, to, toTimestamp) =>
      (Drag(from, fromTimestamp, pe.p, pe.timestamp), DragMove(from, fromTimestamp, pe.p, pe.timestamp, pe.p - to))
  })

  def pointerLeave(pe: PointerLeave) = State[PointerState, GestureEvent] {
    case Drag(from, fromTimestamp, to, toTimestamp) =>
      (Up(), DragAbort(from, fromTimestamp, pe.p, pe.timestamp))
    case _ => (Up(), Noop)
  }

  def handlePointerEvent(pe: PointerEvent): State[PointerState, GestureEvent] = pe match {
    case p: PointerDown => pointerDown(p)
    case p: PointerUp => pointerUp(p)
    case p: PointerMove => pointerMove(p)
    case p: PointerLeave => pointerLeave(p)
  }
}


sealed trait PointerState
case class Up() extends PointerState
case class Down(p: Vec2d, timestamp: Long) extends PointerState
case class Drag(from: Vec2d, fromTimestamp: Long,
                to: Vec2d, toTimestamp: Long) extends PointerState


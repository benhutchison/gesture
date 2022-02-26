package gesture

import cats.*
import cats.data.State
import cats.implicits.*
import org.scalajs.dom.PointerEvent

import scala.util.Try

class GestureProcessor(dragThreshold: Double = 5, ClickDistThreshold: Double = 4.0, ClickTimeThreshold: Double = 400) {

  def pointerDown(pe: PointerDown) = State[PointerState, GestureEvent] {
      case Up() =>
        (Down(pe.p, pe.timestamp), Noop)
      case ps => invalid(pe, ps)
    }

  def invalid(pe: PointerAdt, ps: PointerState) = (ps, Invalid(s"Unexpected input when in state: $ps", pe))

  def pointerUp(pe: PointerUp) = State[PointerState, GestureEvent] {
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
    case ps => invalid(pe, ps)
  }

  def pointerMove(pe: PointerMove) = State[PointerState, GestureEvent] {
    case Up() => (Up(), Noop)
    case ps@Down(p, timestamp) =>
      if (p.distanceTo(pe.p) > dragThreshold)
        (Drag(p, timestamp, pe.p, pe.timestamp), DragStart(p, timestamp, pe.p, pe.timestamp, pe.p - p))
      else
        (ps, Noop)
    case Drag(from, fromTimestamp, to, toTimestamp) =>
      (Drag(from, fromTimestamp, pe.p, pe.timestamp), DragMove(from, fromTimestamp, pe.p, pe.timestamp, pe.p - to))
  }

  def pointerLeave(pe: PointerLeave) = State[PointerState, GestureEvent] {
    case Drag(from, fromTimestamp, to, toTimestamp) =>
      (Up(), DragAbort(from, fromTimestamp, pe.p, pe.timestamp))
    case _ => (Up(), Noop)
  }

  def handlePointerAdt(pa: PointerAdt): State[PointerState, GestureEvent] =
    pa match
      case p: PointerMove => pointerMove(p)
      case p: PointerDown => pointerDown(p)
      case p: PointerUp => pointerUp(p)
      case p: PointerLeave => pointerLeave(p)


  def handlePointerEvent(pe: PointerEvent): State[PointerState, GestureEvent] =
    PointerAdt.fromPointerEvent(pe).fold(State.pure(Noop))(handlePointerAdt(_))
}




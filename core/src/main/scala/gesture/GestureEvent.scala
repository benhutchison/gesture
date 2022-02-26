package gesture

sealed trait GestureEvent
case class Click(p: Vec2d, timestamp: Long) extends GestureEvent
case class DragStart(from: Vec2d, fromTimestamp: Long, to: Vec2d, toTimestamp: Long, delta: Vec2d) extends GestureEvent
case class DragMove(from: Vec2d, fromTimestamp: Long, to: Vec2d, toTimestamp: Long, delta: Vec2d) extends GestureEvent
case class DragComplete(from: Vec2d, fromTimestamp: Long, to: Vec2d, toTimestamp: Long, delta: Vec2d) extends GestureEvent
case class DragAbort(from: Vec2d, fromTimestamp: Long, to: Vec2d, toTimestamp: Long) extends GestureEvent
case class Invalid(msg: String, pointerEvent2: PointerAdt) extends GestureEvent
case object Noop extends GestureEvent

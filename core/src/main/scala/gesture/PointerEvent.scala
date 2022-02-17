package gesture

sealed trait PointerEvent
case class PointerMove(p: Vec2d, timestamp: Long) extends PointerEvent
case class PointerDown(p: Vec2d, timestamp: Long) extends PointerEvent
case class PointerUp(p: Vec2d, timestamp: Long) extends PointerEvent
case class PointerLeave(p: Vec2d, timestamp: Long) extends PointerEvent



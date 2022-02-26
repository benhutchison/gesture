package gesture

sealed trait PointerState
case class Up() extends PointerState
case class Down(p: Vec2d, timestamp: Long) extends PointerState
case class Drag(from: Vec2d, fromTimestamp: Long,
                to: Vec2d, toTimestamp: Long) extends PointerState

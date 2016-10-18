package gesture

import cats._
import cats.data.State
import cats.implicits._

class GestureProcessorSpec extends org.specs2.mutable.Specification {

  val gestureProcessor = new GestureProcessor()

  eg {
    val (s, g) = eventSequence(initialState = Up())(
      PointerDown((0, 0), 0L), PointerUp((2, 2), 10L))

    (s must_== Up()) and (g must_== Click((0, 0), 10L))
  }

  eg {
    val (s, g) = eventSequence(initialState = Up())(
      PointerDown((0, 0), 0L), PointerMove((20, 20), 10L), PointerUp((30, 30), 20L))

    (s must_== Up()) and (g must_== DragComplete((0, 0), 0L, (30, 30), 20L, (10, 10)))
  }

  eg {
    val (s, g) = eventSequence(initialState = Up())(
      PointerDown((0, 0), 0L), PointerMove((20, 20), 10L), PointerLeave((30, 30), 20L))

    (s must_== Up()) and (g must_== DragAbort((0, 0), 0L, (30, 30), 20L))
  }

  def eventSequence(initialState: PointerState)(events: PointerEvent*): (PointerState, GestureEvent) = {
    val tmp = events.toVector.traverseU(gestureProcessor.handlePointerEvent(_))
    val (s, gs) = tmp.run(initialState).value
    (s, gs.last)
  }
}

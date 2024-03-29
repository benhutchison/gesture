package gesture

import cats.*
import cats.data.*
import cats.implicits.*
import munit.*

import scala.scalajs.js

class GestureProcessorSpec extends munit.FunSuite  {

  val gestureProcessor = new GestureProcessor()

  test("Down,Up") {
    val (s, g) = eventSequence(initialState = Up())(
      PointerDown((0, 0), 0L), PointerUp((2, 2), 10L))

    assertEquals(s, Up())
    assertEquals(g, Click((0, 0), 10L))
  }

  test("Down,Move,Up") {
    val (s, g) = eventSequence(initialState = Up())(
      PointerDown((0, 0), 0L), PointerMove((20, 20), 10L), PointerUp((30, 30), 20L))

    assertEquals(s, Up())
    assertEquals(g, DragComplete((0, 0), 0L, (30, 30), 20L, (10, 10)))
  }

  test("Down,Move,Leave") {
    val (s, g) = eventSequence(initialState = Up())(
      PointerDown((0, 0), 0L), PointerMove((20, 20), 10L), PointerLeave((30, 30), 20L))

    assertEquals(s, Up())
    assertEquals(g, DragAbort((0, 0), 0L, (30, 30), 20L))
  }

  def eventSequence(initialState: PointerState)(events: PointerAdt*): (PointerState, GestureEvent) = {
    val tmp = events.toVector.traverse(gestureProcessor.handlePointerAdt(_))
    val (s, gs) = tmp.run(initialState).value
    (s, gs.last)
  }
}

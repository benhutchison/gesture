package gesture

import cats.*
import cats.data.State
import cats.implicits.*
import org.scalajs.dom.PointerEvent


case class GestureAndRegions[R](gesture: GestureEvent, from: Option[R], to: Option[R])

abstract class GestureAndRegionProcessor[R](dragThreshold: Double = 5, ClickDistThreshold: Double = 4.0, ClickTimeThreshold: Double = 400):
  val gestureProcess = new GestureProcessor(dragThreshold, ClickDistThreshold, ClickTimeThreshold)

  type PointerRegionState = (PointerState, Option[R])

  def handlePointerEvent(pe: PointerEvent) =
    State[PointerRegionState, GestureAndRegions[R]](
      (ps, optRegion) =>
        val (ps2, g) = gestureProcess.handlePointerEvent(pe).run(ps).value
        g match
          case DragStart(from, _, to, _, _) =>
            val fromR = regionSearch(from)
            val s = (ps2, fromR)
            val a = GestureAndRegions(g, fromR, regionSearch(to))
            (s, a)
          case DragMove(_, _, to, _, _) =>
            val s = (ps2, optRegion)
            val a = GestureAndRegions(g, optRegion, regionSearch(to))
            (s, a)
          case DragComplete(_, _, to, _, _) =>
            val s = (ps2, None)
            val a = GestureAndRegions(g, optRegion, bottomRegionSearch(to, optRegion))
            (s, a)
          case DragAbort(_, _, to, _) =>
            val s = (ps2, None)
            val a = GestureAndRegions(g, optRegion, regionSearch(to))
            (s, a)
          case Click(p, _) =>
            val s = (ps2, None)
            val a = GestureAndRegions(g, regionSearch(p), None)
            (s, a)
          case other =>
            val s = (ps2, None)
            val a = GestureAndRegions[R](g, None, None)
            (s, a)
    )

  def regionSearch(p: Vec2d): Option[R]

  def bottomRegionSearch(p: Vec2d, excludeRegionFromSearch: Option[R] = None): Option[R]


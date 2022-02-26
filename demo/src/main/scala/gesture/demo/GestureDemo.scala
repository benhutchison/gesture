package gesture
package demo

import scala.util.Random

import Predef.{any2stringadd => _, _}

import cats._
import cats.data.State
import cats.implicits._
import cats.syntax.all._

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html

object GestureDemo:

  def main(args: Array[String]): Unit =
    val doc = dom.document
    val root = doc.getElementById("root")
    val canvas = doc.createElement("canvas").asInstanceOf[html.Canvas]
    root.appendChild(canvas)
    new GestureCanvas(canvas)

class GestureCanvas(canvas: html.Canvas):

  val Width = 162.0
  val Height = 100.0
  val RedColorString = "rgb(255, 150, 150)"

  var rectangles: Vector[Rect] = Vector()

  val gestureRegionProcessor = new GestureAndRegionProcessor[Rect]()

  var pointerAndRegionState = (Up(): PointerState, Option.empty[Rect])

  canvas.width = dom.window.innerWidth.toInt
  canvas.height = dom.window.innerHeight.toInt - 50

  val context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]


  def draw(): Unit =
    val rect = canvas.getBoundingClientRect()
    context.clearRect(0, 0, canvas.width, canvas.height)
    rectangles.reverseIterator.foreach(r =>
        context.fillStyle = r.cssColorString
        //we adjust from client coordinates into canvas coordinates here
        context.fillRect(r.topLeft._1 - rect.left, r.topLeft._2 - rect.top, r.width, r.height))


  List("pointerdown", "pointermove", "pointerup", "pointerleave").foreach(name =>
      canvas.addEventListener[dom.PointerEvent](name, (event: dom.PointerEvent) =>
        handlePointerEvent(event)))

  def search: Vec2d => Option[Rect] = (p) => rectangles.find(_.contains(p))

  def handlePointerEvent(pe: PointerEvent) = {
    val (newState, gestureAndRegions) = gestureRegionProcessor.handlePointerEvent(pe, search).run(pointerAndRegionState).value

    pointerAndRegionState = newState
    interpret(gestureAndRegions)
  }

  def interpret(gr: GestureAndRegions[Rect]) =
    gr match
      case GestureAndRegions(Click(p, timestamp), None, None) =>
        def randLightValue = 180 + Random.nextInt(60)
        val randomColor = s"rgb($randLightValue, $randLightValue, $randLightValue)"
        val r = new Rect(p, Width, Height, randomColor)
        rectangles = rectangles :+ r
        draw()
      case GestureAndRegions(d: DragMove, Some(Rect(_, _, _, _, id)), _) =>
        rectangles = rectangles.map(r =>
          if (r.id == id)
            r.copy(topLeft = r.topLeft + d.delta)
          else r)
      case GestureAndRegions(d: DragAbort, Some(Rect(_, _, _, _, id)), _) =>
        rectangles = rectangles.filterNot(_.id == id)
      case GestureAndRegions(d: DragComplete, Some(Rect(_, _, _, _, srcId)), Some(Rect(_, _, _, _, targetId))) =>
        rectangles = rectangles.map(r =>
          if (srcId != targetId && r.id == targetId)
            r.copy(cssColorString = RedColorString)
          else r)
      case _ => Noop

    draw()


  //see Exercize.md
  def countDragsToRect(gestureAndRegions: GestureAndRegions[Rect]): State[Int, Boolean] = ???

  def lift[T, S, A](s: State[T, A], get: S => T, set: (S, T) => S): State[S, A] = ???

case class Rect(topLeft: Vec2d, width: Double, height: Double, cssColorString: String,
  //define an ID to keep track of, and retrieve, the rectangle as it gets dragged around
  id: Long = Random.nextLong()):

  def contains(p: Vec2d) = p._1 >= topLeft._1 && p._2 >= topLeft._2 && p._1 < topLeft._1 + width && p._2 < topLeft._2 + height


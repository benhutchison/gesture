# gesture

Purely functional recognition of Drag and Click gestures over a W3C Pointer Event -compatible API

Built on top of the `State` monad provided by the [Cats library](https://github.com/non/cats).



## gesture core

Contains the library code:

* `GestureProcessor` converts a series of `PointerEvent`s into a series of `GestureEvent`s, while updating a Finite State Machine 
represented by `PointerState`.
 
* Optionally, `GestureAndRegionProcessor[R]` can extend the functionality to tracking the *region `R`* where the gesture
began and completed, if any. The region itself is provided by the user of the library an can be any type, so long a there is
a search function `(Double, Double) => Option[R]` to find the region `R` at a coordinate, if any.

* Coordinates are modeled as `(Double, Double)`


Published for both Scala.jvm and Scala.js.

SBT: `"com.github.benhutchison" %%% "gesture" % "0.1"`
 
## gesture demo
 
Shows a working demo of the recognition system, implemented over an HTML canvas. Colored rectangles can be created and dragged around the screen.

To try it out, clone the project, run `sbt demo/fullOptJS`, and then load file `demo/demo-opt.html` in a browser.

The demo uses the reliable [Points polyfill](http://rich-harris.github.io/Points) developed by Rich Harris to provide
Pointer Events across browsers/devices.
## Learning Exercise

This learning exercise is intended to build skills composing and lensing `State`.

Currently the demo [processes each `PointerEvent` and returns the new `PointerRegionState` and the
 `GestureAndRegions[Rect]` if any](src/main/scala/gesture/demo/GestureDemo.scala#L74).

What if we wanted to count the number of times a drag completed inside a rectangle? Then we would have a second
piece of state, the counter, and a second state update, being the counter increment, dependent upon the output
of the first state process.

### 1. Counter State

Implement the method [`countDragsToRect(gestureAndRegions): State[Int, Boolean]`](src/main/scala/gesture/demo/GestureDemo.scala#L105).
The action should return true of the counter was incremented.

### 2. Combine Gesture and Counter States

We can model the combined state using a tuple `(PointerRegionState, Int)`. How can we write a `State` monad representing
both? The trick is to use *lenses* to translate operations over each sub-state to an operations over the common state.

### 2.1 Lens lift

Implement [`lift[T, S, A](s: State[T, A], get: S => T, set: (S, T) => S): State[S, A]`](src/main/scala/gesture/demo/GestureDemo.scala#L107)
to apply a lens over a substate T to convert into a `State[S, A]`.

Define lens `get`/`set` operations between the tupled state and the two sub-states.

### 2.2 Sequence two State updates

Use a for expression to sequence `handlePointerEvent` with `countDragsToRect`. You'll need to use lenses and the `lift`
operation to convert both `State` updates into a common data "currency".

Your for-expression will itself be a `State`, whose action should yield `(GestureAndRegions[Rect], Int)`
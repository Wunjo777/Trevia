package com.example.trevia.utils

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs

enum class PointerRequisite
{
    LessThan, EqualTo, GreaterThan, None
}

/**
 * A gesture detector for rotation, panning, and zoom. Once touch slop has been reached, the
 * user can use rotation, panning and zoom gestures. [onGesture] will be called when any of the
 * rotation, zoom or pan occurs, passing the rotation angle in degrees, zoom in scale factor and
 * pan as an offset in pixels. Each of these changes is a difference between the previous call
 * and the current gesture. This will consume all position changes after touch slop has
 * been reached. [onGesture] will also provide centroid of all the pointers that are down.
 *
 * After gesture started  when last pointer is up [onGestureEnd] is triggered.
 *
 * @param consume flag consume [PointerInputChange]s this gesture uses. Consuming
 * returns [PointerInputChange.isConsumed] true which is observed by other gestures such
 * as drag, scroll and transform. When this flag is true other gesture don't receive events.
 * @param pass The enumeration of passes where [PointerInputChange]
 * traverses up and down the UI tree.
 * @param onGestureStart callback for notifying transform gesture has started with initial
 * pointer
 * @param onGesture callback for passing centroid, pan, zoom, rotation and  main pointer and
 * pointer size to caller. Main pointer is the one that touches screen first. If it's lifted
 * next one that is down is the main pointer.
 * @param onGestureEnd callback that notifies last pointer is up and gesture is ended if it's
 * started by fulfilling requisite.
 *
 */
suspend fun PointerInputScope.detectTransformGestures(
    panZoomLock: Boolean = false,
    consume: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = {},
    onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) -> Unit,
    onGestureEnd: (PointerInputChange) -> Unit = {}
)
{
    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false

        // Wait for at least one pointer to press down, and set first contact position
        val down: PointerInputChange = awaitFirstDown(
            requireUnconsumed = false,
            pass = pass
        )
        onGestureStart(down)

        var pointer = down
        // Main pointer is the one that is down initially
        var pointerId = down.id

        do
        {
            val event = awaitPointerEvent(pass = pass)

            // If any position change is consumed from another PointerInputChange
            // or pointer count requirement is not fulfilled
            val canceled =
                event.changes.any { it.isConsumed }

            if (!canceled)
            {

                // Get pointer that is down, if first pointer is up
                // get another and use it if other pointers are also down
                // event.changes.first() doesn't return same order
                val pointerInputChange =
                    event.changes.firstOrNull { it.id == pointerId }
                        ?: event.changes.first()

                // Next time will check same pointer with this id
                pointerId = pointerInputChange.id
                pointer = pointerInputChange

                val zoomChange = event.calculateZoom()
                val rotationChange = event.calculateRotation()
                val panChange = event.calculatePan()

                if (!pastTouchSlop)
                {
                    zoom *= zoomChange
                    rotation += rotationChange
                    pan += panChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    val rotationMotion =
                        abs(rotation * PI.toFloat() * centroidSize / 180f)
                    val panMotion = pan.getDistance()

                    if (zoomMotion > touchSlop ||
                        rotationMotion > touchSlop ||
                        panMotion > touchSlop
                    )
                    {
                        pastTouchSlop = true
                        lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                    }
                }

                if (pastTouchSlop)
                {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                    if (effectiveRotation != 0f ||
                        zoomChange != 1f ||
                        panChange != Offset.Zero
                    )
                    {
                        onGesture(
                            centroid,
                            panChange,
                            zoomChange,
                            effectiveRotation,
                            pointer,
                            event.changes
                        )
                    }

                    if (consume)
                    {
                        event.changes.forEach {
                            if (it.positionChanged())
                            {
                                it.consume()
                            }
                        }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })
        onGestureEnd(pointer)
    }
}

// Source - https://stackoverflow.com/questions/71156016/how-to-use-detecttransformgestures-but-not-consuming-all-pointer-event
// Posted by IacobIonut, modified by community. See post 'Timeline' for change history
// Retrieved 2025-12-08, License - CC BY-SA 4.0
fun Modifier.tapAndGesture(
    key: Any? = Unit,
    onTap: ((Offset) -> Unit)? = null,
    onDoubleTap: ((Offset) -> Unit)? = null,
    onGesture: ((centeroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit)? = null,
    scrollEnabled: MutableState<Boolean> = mutableStateOf(false)
) = composed(
    factory = {
        val scope = rememberCoroutineScope()

        val gestureModifier = Modifier.pointerInput(key) {
            /**
             * [detectTransformGestures]
             * @author SmartToolFactory
             * href: https://github.com/SmartToolFactory/Compose-Extended-Gestures
             */
            detectTransformGestures(
                consume = false,
                onGesture = { centroid: Offset,
                              pan: Offset,
                              zoom: Float,
                              rotation: Float,
                              _: PointerInputChange,
                              changes: List<PointerInputChange> ->
                    scope.launch {
                        onGesture?.invoke(centroid, pan, zoom, rotation)
                    }
                    changes.forEach {
                        // Consume if scroll gestures are not possible
                        if (!scrollEnabled.value) it.consume()
                    }
                }
            )
        }
        val tapModifier = Modifier.pointerInput(key) {
            detectTapGestures(
                onDoubleTap = onDoubleTap,
                onTap = onTap
            )
        }
        then(gestureModifier.then(tapModifier))
    }
)
package com.iptvplayer.xtreamiptv.myiptvpro.Main.Other

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.view.MotionEvent
import android.widget.FrameLayout
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen
import kotlin.math.abs

class InterceptFrameLayout(context: Context) : FrameLayout(context) {

    private var initialX = 0f
    private var initialY = 0f
    private var initialRawX = 0f
    private var initialRawY = 0f
    private val handler = Handler(Looper.getMainLooper())
    private var isLongPressDetected = false
    private val longPressTimeout = 500L
    private val slop = 20f
    private var storedTouchListener: OnTouchListener? = null
    var onLongPressDetected: ((MotionEvent) -> Unit)? = null
    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
        storedTouchListener = l
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = ev.x
                initialY = ev.y
                initialRawX = ev.rawX
                initialRawY = ev.rawY
                isLongPressDetected = false
                handler.removeCallbacks(longPressRunnable)
                handler.postDelayed(longPressRunnable, longPressTimeout)
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLongPressDetected) {
                    // After long press is detected, allow dragging by intercepting
                    return true
                } else if (abs(ev.x - initialX) > slop || abs(ev.y - initialY) > slop) {
                    // Cancel long press if user moves before long press timeout
                    handler.removeCallbacks(longPressRunnable)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                if (isLongPressDetected) {
                    // Reset after drag ends
                    isLongPressDetected = false
                    (context as? HomeLaunchScreen)?.isDraggingItem = false
                }
            }
        }

        return isLongPressDetected
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Once we start intercepting (long press detected), forward events to our touch listener for dragging
        if (isLongPressDetected) {
            // Forward the event to the wrapper's touch listener (DraggableTouchListener)
            // This allows dragging to work after long press
            return storedTouchListener?.onTouch(this, event) ?: true
        }
        return super.onTouchEvent(event)
    }

    private val longPressRunnable = Runnable {
        isLongPressDetected = true
        (context as? HomeLaunchScreen)?.isDraggingItem = true
        parent?.requestDisallowInterceptTouchEvent(true)

        // Vibrate for feedback
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(50)

        // IMPORTANT: Manually prime the listener since it might have missed ACTION_DOWN
        // while the child view was consuming touch.
        val downEvent = MotionEvent.obtain(
            System.currentTimeMillis(), System.currentTimeMillis(),
            MotionEvent.ACTION_DOWN, initialRawX, initialRawY, 0
        )
        onLongPressDetected?.invoke(downEvent)
        // Also forward to our touch listener to initialize drag state
        storedTouchListener?.onTouch(this, downEvent)
        downEvent.recycle()
    }
}
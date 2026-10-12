package com.iptvplayer.xtreamiptv.myiptvpro.Main.Other

import android.view.MotionEvent
import android.view.View
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity.HomeLaunchScreen
import kotlin.math.abs

class DraggableTouchListener(
    private val mainActivity: HomeLaunchScreen,
    private val onMove: (Float, Float) -> Unit,
    private val onFinish: () -> Unit,
    private val onLongClick: () -> Unit = {}
) : View.OnTouchListener {

    private var initialX: Float = 0f
    private var initialY: Float = 0f
    private var dX: Float = 0f
    private var dY: Float = 0f
    
    private var isDragging = false
    private var hasMoved = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = view.x
                initialY = view.y
                dX = view.x - event.rawX
                dY = view.y - event.rawY
                
                isDragging = false
                hasMoved = false


                if (mainActivity.isDraggingItem) {
                    isDragging = true
                }

                return false 
            }
            MotionEvent.ACTION_MOVE -> {
                if (mainActivity.isDraggingItem && !isDragging) {
                    initialX = view.x
                    initialY = view.y
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    isDragging = true
                }

                if (isDragging) {
                    val currentDX = abs(event.rawX + dX - initialX)
                    val currentDY = abs(event.rawY + dY - initialY)
                    
                    if (currentDX > 50 || currentDY > 50) {
                        hasMoved = true
                    }
                    
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY
                    view.x = newX
                    view.y = newY
                    onMove(newX, newY)
                    return true 
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging || mainActivity.isDraggingItem) {
                    isDragging = false
                    mainActivity.isDraggingItem = false
                    
                    if (!hasMoved && event.action == MotionEvent.ACTION_UP) {
                        onLongClick()
                    } else {
                        onFinish()
                    }
                    return true 
                }
            }
        }
        return false
    }
}

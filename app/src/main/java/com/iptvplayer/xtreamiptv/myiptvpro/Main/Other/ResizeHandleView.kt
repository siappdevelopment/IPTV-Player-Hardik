package com.iptvplayer.xtreamiptv.myiptvpro.Main.Other

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.iptvplayer.xtreamiptv.myiptvpro.Main.Helper.HomeGridHelper
import kotlin.math.abs

class ResizeHandleView(
    context: Context,
    private val targetView: View,
    private val gridHelper: HomeGridHelper,
    private val onResizeFinish: (Int, Int) -> Unit
) : View(context) {

    private val handleRadius = 20f
    private val paint = Paint().apply {
        color = Color.parseColor("#4285F4") // Google Blue
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private var activeHandle = -1 // 0: Right, 1: Bottom
    private var initialSpanX = 1
    private var initialSpanY = 1

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw Right Handle
        canvas.drawCircle(width.toFloat(), height / 2f, handleRadius, paint)
        
        // Draw Bottom Handle
        canvas.drawCircle(width / 2f, height.toFloat(), handleRadius, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                
                // Check Right Handle
                if (abs(x - width) < 50 && abs(y - height / 2f) < 50) {
                    activeHandle = 0
                    initialSpanX = (targetView.width / gridHelper.cellWidth)
                    return true
                }
                
                // Check Bottom Handle
                if (abs(x - width / 2f) < 50 && abs(y - height) < 50) {
                    activeHandle = 1
                    initialSpanY = (targetView.height / gridHelper.cellHeight)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (activeHandle == 0) {
                    val rawWidthPx = event.x.toInt()
                    // Calculate span based on pixels, ensuring it doesn't exceed grid or push off screen
                    val currentCellX = gridHelper.getCellX(targetView.x)
                    val maxAvailableSpanX = HomeGridHelper.Companion.GRID_COLUMNS - currentCellX
                    val newSpanX = gridHelper.getSpanFromPixelsX(rawWidthPx).coerceIn(1, maxAvailableSpanX)
                    
                    val newWidth = gridHelper.getSpanWidth(newSpanX)
                    val lp = targetView.layoutParams as FrameLayout.LayoutParams
                    lp.width = newWidth
                    targetView.layoutParams = lp
                    
                    val hlp = layoutParams as FrameLayout.LayoutParams
                    hlp.width = newWidth
                    layoutParams = hlp
                    invalidate()
                } else if (activeHandle == 1) {
                    val rawHeightPx = event.y.toInt()
                    // Calculate span based on pixels
                    val currentCellY = gridHelper.getCellY(targetView.y)
                    val maxAvailableSpanY = HomeGridHelper.Companion.GRID_ROWS - currentCellY
                    val newSpanY = gridHelper.getSpanFromPixelsY(rawHeightPx).coerceIn(1, maxAvailableSpanY)
                    
                    val newHeight = gridHelper.getSpanHeight(newSpanY)
                    val lp = targetView.layoutParams as FrameLayout.LayoutParams
                    lp.height = newHeight
                    targetView.layoutParams = lp
                    
                    val hlp = layoutParams as FrameLayout.LayoutParams
                    hlp.height = newHeight
                    layoutParams = hlp
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (activeHandle != -1) {
                    // Calculate final spans based on actual pixel dimensions
                    val finalSpanX = gridHelper.getSpanFromPixelsX(targetView.width)
                    val finalSpanY = gridHelper.getSpanFromPixelsY(targetView.height)
                    onResizeFinish(finalSpanX, finalSpanY)
                    activeHandle = -1
                }
            }
        }
        return super.onTouchEvent(event)
    }
}

package com.iptvplayer.xtreamiptv.myiptvpro.Main.Helper

import android.content.Context
import android.util.DisplayMetrics

class HomeGridHelper(private val context: Context) {

    companion object {
        const val GRID_COLUMNS = 4
        const val GRID_ROWS = 6
    }

    private val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    private val screenWidth = displayMetrics.widthPixels
    private val screenHeight = displayMetrics.heightPixels

    // Total height of the widget_container (screen minus approximate dock height + status bar/nav bar)
    // Dock is 90dp + 10dp margin = 100dp.
    // We'll subtract ~120dp to be safe and ensure Row 5 is fully visible.
    private val dockAreaPx = dpToPx(130)
    var availableHeight = screenHeight - dockAreaPx
        private set

    val cellWidth = screenWidth / GRID_COLUMNS
    var cellHeight = availableHeight / GRID_ROWS
        private set

    /**
     * Re-fits the rows to the real height of the page container. The screen height is only an
     * estimate: once the system bar insets are padded around the pager the container is shorter,
     * and the last rows would end up behind the dock. Returns true when the cell height changed.
     */
    fun setContainerHeight(containerHeightPx: Int): Boolean {
        if (containerHeightPx <= 0) return false
        val newAvailable = containerHeightPx - dockAreaPx
        if (newAvailable <= 0 || newAvailable == availableHeight) return false
        availableHeight = newAvailable
        cellHeight = newAvailable / GRID_ROWS
        return true
    }

    fun pxToDp(px: Int): Int = (px / (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    fun dpToPx(dp: Int): Int = (dp * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

    fun getPixelX(cellX: Int): Float = (cellX * cellWidth).toFloat()
    fun getPixelY(cellY: Int): Float = (cellY * cellHeight).toFloat()

    fun getCellX(pixelX: Float): Int {
        // Use proper rounding to get the nearest cell
        val cell = Math.round(pixelX / cellWidth).toInt()
        return cell.coerceIn(0, GRID_COLUMNS - 1)
    }

    fun getCellY(pixelY: Float): Int {
//        val cell = (pixelY / cellHeight).toInt()
        val cell = Math.round(pixelY / cellHeight).toInt()
        return cell.coerceIn(0, GRID_ROWS - 1)
    }

    /**
     * Standard Android Formula for calculating span from size in DP:
     * Cells = (Size + 30) / 70
     */
    fun getSpanFromDp(sizeDp: Int): Int = ((sizeDp + 30) / 70).coerceAtLeast(1)

    /**
     * Snap a pixel coordinate to the nearest cell's top-left pixel
     */
    fun snapX(pixelX: Float): Float = (getCellX(pixelX + cellWidth / 2) * cellWidth).toFloat()
    fun snapY(pixelY: Float): Float = (getCellY(pixelY + cellHeight / 2) * cellHeight).toFloat()

    /**
     * Get dimensions in pixels for a given span
     */
    fun getSpanWidth(spanX: Int): Int = spanX * cellWidth
    fun getSpanHeight(spanY: Int): Int = spanY * cellHeight

    /**
     * Map raw pixels to the number of grid cells they should occupy.
     * Uses a 0.5 threshold for rounding to the nearest cell.
     */
    fun getSpanFromPixelsX(pixels: Int): Int =
        Math.round(pixels.toFloat() / cellWidth).toInt().coerceIn(1, GRID_COLUMNS)

    fun getSpanFromPixelsY(pixels: Int): Int =
        Math.round(pixels.toFloat() / cellHeight).toInt().coerceIn(1, GRID_ROWS)

    /**
     * Get span (cells) needed to fit the given size in dp.
     * Uses pixel size so widget gets same physical size as system launcher.
     */
    fun getSpanFromDpSize(widthDp: Int, heightDp: Int): Pair<Int, Int> {
        val widthPx = dpToPx(widthDp)
        val heightPx = dpToPx(heightDp)
        val spanX = getSpanFromPixelsX(widthPx)
        val spanY = getSpanFromPixelsY(heightPx)
        return spanX to spanY
    }

    /** Max width/height in pixels a widget can have (fits on screen). */
    fun getMaxWidgetWidthPx(): Int = getSpanWidth(GRID_COLUMNS)
    fun getMaxWidgetHeightPx(): Int = getSpanHeight(GRID_ROWS)

    /**
     * Clamp requested widget size to fit on screen so widget never overflows.
     * Returns (clampedPixelWidth, clampedPixelHeight); use pxToDp for options passed to widget.
     */
    fun clampWidgetSizeToScreen(pixelWidth: Int, pixelHeight: Int): Pair<Int, Int> {
        val maxW = getMaxWidgetWidthPx()
        val maxH = getMaxWidgetHeightPx()
        return (pixelWidth.coerceIn(1, maxW) to pixelHeight.coerceIn(1, maxH))
    }
}
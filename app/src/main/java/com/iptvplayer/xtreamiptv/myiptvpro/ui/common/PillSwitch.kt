package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.iptvplayer.xtreamiptv.myiptvpro.R

/**
 * A pill-shaped on/off switch: the track is the `switch_track` state drawable (blue when checked), the
 * thumb is the white circle `switch_thumb`, slid between the two ends with a short animation.
 * Behaves like any [CompoundButton] (`isChecked`, `setOnCheckedChangeListener`, saved state).
 */
class PillSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : CompoundButton(context, attrs, defStyleAttr) {

    private val track: Drawable = ContextCompat.getDrawable(context, R.drawable.switch_track)!!.mutate()
    private val thumb: Drawable = ContextCompat.getDrawable(context, R.drawable.switch_thumb)!!.mutate()
    private var position = if (isChecked) 1f else 0f
    private var animator: ValueAnimator? = null

    init {
        background = null
        track.callback = this
        thumb.callback = this
    }

    override fun setChecked(checked: Boolean) {
        val changed = checked != isChecked
        super.setChecked(checked)
        if (!changed) return
        if (isLaidOut && isShown) animateTo(if (checked) 1f else 0f) else snapTo(if (checked) 1f else 0f)
    }

    private fun animateTo(target: Float) {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(position, target).apply {
            duration = ANIMATION_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                position = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun snapTo(target: Float) {
        animator?.cancel()
        position = target
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSize(track.intrinsicWidth + paddingLeft + paddingRight, widthMeasureSpec)
        val h = resolveSize(track.intrinsicHeight + paddingTop + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        track.setBounds(left, top, right, bottom)
        track.draw(canvas)

        val size = thumb.intrinsicHeight.coerceAtMost(bottom - top)
        val inset = (bottom - top - size) / 2
        val travel = (right - left) - 2 * inset - size
        val x = left + inset + (travel * position).toInt()
        val y = top + inset
        thumb.setBounds(x, y, x + size, y + size)
        thumb.draw(canvas)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        track.state = drawableState
        thumb.state = drawableState
        invalidate()
    }

    override fun verifyDrawable(who: Drawable): Boolean = who === track || who === thumb || super.verifyDrawable(who)

    override fun getAccessibilityClassName(): CharSequence = "android.widget.Switch"

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    private companion object {
        const val ANIMATION_MS = 180L
    }
}

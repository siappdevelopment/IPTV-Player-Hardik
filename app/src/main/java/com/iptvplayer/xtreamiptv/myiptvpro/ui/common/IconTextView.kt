package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import androidx.appcompat.widget.AppCompatTextView
import com.iptvplayer.xtreamiptv.myiptvpro.R

/**
 * A text with leading/trailing icon drawables that are centred *together* with the text, the way the design's
 * `display:flex; align-items:center; justify-content:center; gap:N` buttons are laid out. A normal TextView pins
 * `drawableStart` / `drawableEnd` to the view's edges.
 *
 * Declare icons with `drawableStart` / `drawableEnd` as usual; `drawablePadding` is the gap.
 */
class IconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    /** `app:iconSize`: forces both icons to this square size (the design sets glyph sizes independently of the asset). */
    private var iconSizePx = 0

    init {
        context.obtainStyledAttributes(attrs, R.styleable.IconTextView).use { a ->
            iconSizePx = a.getDimensionPixelSize(R.styleable.IconTextView_iconSize, 0)
        }
        // drawableStart / drawableEnd from XML are applied by TextView without going through the overrides
        takeIcons(compoundDrawablesRelative)
    }

    // Held in view tags: TextView's constructor calls the overrides below before this class's own fields are initialised.
    private var startIcon: Drawable?
        get() = getTag(R.id.tag_icon_start) as? Drawable
        set(value) = setTag(R.id.tag_icon_start, value)
    private var endIcon: Drawable?
        get() = getTag(R.id.tag_icon_end) as? Drawable
        set(value) = setTag(R.id.tag_icon_end, value)

    override fun setCompoundDrawablesRelative(start: Drawable?, top: Drawable?, end: Drawable?, bottom: Drawable?) {
        takeIcons(arrayOf(start, top, end, bottom))
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        start: Drawable?,
        top: Drawable?,
        end: Drawable?,
        bottom: Drawable?,
    ) {
        start?.setBounds(0, 0, start.intrinsicWidth, start.intrinsicHeight)
        end?.setBounds(0, 0, end.intrinsicWidth, end.intrinsicHeight)
        takeIcons(arrayOf(start, top, end, bottom))
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(start: Int, top: Int, end: Int, bottom: Int) {
        fun load(res: Int) = if (res == 0) null else androidx.core.content.ContextCompat.getDrawable(context, res)
        setCompoundDrawablesRelativeWithIntrinsicBounds(load(start), load(top), load(end), load(bottom))
    }

    private fun takeIcons(drawables: Array<Drawable?>) {
        startIcon = drawables[0]
        endIcon = drawables[2]
        val tint = compoundDrawableTintList
        listOfNotNull(startIcon, endIcon).forEach { icon ->
            if (icon.bounds.isEmpty) icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
            if (tint != null) icon.setTintList(tint)
        }
        super.setCompoundDrawablesRelative(null, drawables[1], null, drawables[3])
        invalidate()
    }

    private fun iconWidth(icon: Drawable) = if (iconSizePx > 0) iconSizePx else icon.bounds.width()

    private fun iconHeight(icon: Drawable) = if (iconSizePx > 0) iconSizePx else icon.bounds.height()

    private fun iconsWidth(): Int {
        var width = 0
        startIcon?.let { width += iconWidth(it) + compoundDrawablePadding }
        endIcon?.let { width += iconWidth(it) + compoundDrawablePadding }
        return width
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        startIcon?.state = drawableState
        endIcon?.state = drawableState
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY) {
            setMeasuredDimension(measuredWidth + iconsWidth(), measuredHeight)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val start = startIcon
        val end = endIcon
        if (start == null && end == null) {
            super.onDraw(canvas)
            return
        }
        val textWidth = layout?.let { l -> (0 until l.lineCount).maxOfOrNull { l.getLineWidth(it) } } ?: 0f
        val gap = compoundDrawablePadding
        val startWidth = start?.let { iconWidth(it) + gap } ?: 0
        val endWidth = end?.let { iconWidth(it) + gap } ?: 0
        val available = width - paddingLeft - paddingRight
        val group = startWidth + textWidth + endWidth
        val groupLeft = paddingLeft + ((available - group) / 2f).coerceAtLeast(0f)
        // the text is centred by gravity inside the view; move it to where the group puts it
        val centredTextLeft = paddingLeft + (available - textWidth) / 2f
        val desiredTextLeft = groupLeft + startWidth

        canvas.save()
        canvas.translate(desiredTextLeft - centredTextLeft, 0f)
        super.onDraw(canvas)
        canvas.restore()

        compoundDrawableTintList?.let { tint -> listOfNotNull(start, end).forEach { it.setTintList(tint) } }
        start?.let { drawIcon(canvas, it, groupLeft) }
        end?.let { drawIcon(canvas, it, desiredTextLeft + textWidth + gap) }
    }

    private fun drawIcon(canvas: Canvas, icon: Drawable, left: Float) {
        val top = (height - iconHeight(icon)) / 2f
        icon.setBounds(0, 0, iconWidth(icon), iconHeight(icon))
        canvas.save()
        canvas.translate(left, top)
        icon.draw(canvas)
        canvas.restore()
    }
}

package com.iptvplayer.xtreamiptv.myiptvpro.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged

/** Adds the system bar insets to this view's own padding (edge-to-edge support). */
fun View.applySystemBarInsets(top: Boolean = true, bottom: Boolean = true, sides: Boolean = true) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val bars = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
        )
        view.updatePadding(
            left = initialLeft + if (sides) bars.left else 0,
            top = initialTop + if (top) bars.top else 0,
            right = initialRight + if (sides) bars.right else 0,
            bottom = initialBottom + if (bottom) bars.bottom else 0,
        )
        windowInsets
    }
    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun View.show(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

/** Calls [onChange] with the trimmed text on every edit. */
fun EditText.onTrimmedTextChanged(onChange: (String) -> Unit) {
    doOnTextChanged { text, _, _, _ -> onChange(text?.toString()?.trim().orEmpty()) }
}

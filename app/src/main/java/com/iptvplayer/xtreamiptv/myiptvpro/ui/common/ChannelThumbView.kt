package com.iptvplayer.xtreamiptv.myiptvpro.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.res.use
import coil3.dispose
import coil3.load
import coil3.request.crossfade
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ViewThumbBinding

/**
 * A logo tile: the host's background (rounded gradient) is the base, the channel logo is loaded on top,
 * and a glyph placeholder shows until a logo is available (or when there is none / it fails to load).
 * The tile clips itself to the background's outline so logos never leak past the rounded corners.
 */
class ChannelThumbView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewThumbBinding

    init {
        binding = ViewThumbBinding.inflate(LayoutInflater.from(context), this)
        clipToOutline = true
        context.obtainStyledAttributes(attrs, R.styleable.ChannelThumbView).use { a ->
            a.getDimensionPixelSize(R.styleable.ChannelThumbView_tileSize, -1).takeIf { it > 0 }?.let { size ->
                binding.thumbPlaceholderTile.layoutParams = binding.thumbPlaceholderTile.layoutParams.apply {
                    width = size
                    height = size
                }
            }
            a.getDimensionPixelSize(R.styleable.ChannelThumbView_tileIconSize, -1).takeIf { it > 0 }?.let { size ->
                binding.thumbPlaceholderIcon.layoutParams = binding.thumbPlaceholderIcon.layoutParams.apply {
                    width = size
                    height = size
                }
            }
            binding.thumbCaption.visibility =
                if (a.getBoolean(R.styleable.ChannelThumbView_showCaption, false)) VISIBLE else GONE
        }
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    /**
     * Resets every state first, then: no URL -> placeholder; URL -> placeholder while loading, the logo alone
     * (filling the tile) on success, the placeholder again on error. dispose() cancels a stale request so a
     * recycled holder never receives the previous channel's result.
     */
    fun bind(logoUrl: String?, @DrawableRes placeholder: Int = R.drawable.ic_live_tv) {
        binding.thumbImage.dispose()
        binding.thumbImage.setImageDrawable(null)
        binding.thumbPlaceholderIcon.setImageResource(placeholder)
        showPlaceholder(loading = !logoUrl.isNullOrBlank())
        if (logoUrl.isNullOrBlank()) return
        binding.thumbImage.load(logoUrl) {
            crossfade(true)
            listener(
                onSuccess = { _, _ -> showLogo() },
                onError = { _, _ ->
                    binding.thumbImage.setImageDrawable(null)
                    showPlaceholder()
                },
            )
        }
    }

    private fun showLogo() {
        binding.thumbImage.visibility = VISIBLE
        binding.thumbPlaceholder.visibility = GONE
    }

    // INVISIBLE (not GONE) while loading: Coil sizes the request from the laid-out view, so a GONE one never loads.
    private fun showPlaceholder(loading: Boolean = false) {
        binding.thumbImage.visibility = if (loading) INVISIBLE else GONE
        binding.thumbPlaceholder.visibility = VISIBLE
    }
}

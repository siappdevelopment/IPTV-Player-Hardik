package com.iptvplayer.xtreamiptv.myiptvpro.ui.settings

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.DialogAboutBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.DialogRateBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.DialogRefreshBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemAboutStatBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemRefreshRowBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemShareTargetBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.SheetShareBinding
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Dialogs
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Sheets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Messages
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** The Settings sub-dialogs from the design: refresh progress, rate, about and share. */
object SettingsDialogs {

    private enum class RowState { WAITING, ACTIVE, DONE, FAILED }

    private const val FILE_ROW_PAUSE_MS = 250L
    private const val COPIED_RESET_MS = 2_000L
    private const val DISABLED_ALPHA = 0.4f

    // --- Refresh playlists --------------------------------------------------------------------

    fun refresh(activity: AppCompatActivity, anchor: View) {
        val inflater = LayoutInflater.from(activity)
        val b = DialogRefreshBinding.inflate(inflater)
        val dialog = Dialogs.showCard(activity, b.root, cancelable = false)
        var job: Job? = null
        var finished = false
        b.btnAction.setOnClickListener {
            job?.cancel()
            dialog.dismiss()
            if (finished) {
                Messages.show(anchor, R.string.toast_playlists_refreshed, R.drawable.ic_sync, R.color.color_accent_light)
            }
        }

        job = activity.lifecycleScope.launch {
            val playlists = activity.container.playlistRepository.observePlaylists().first()
            val rows = playlists.mapIndexed { index, playlist ->
                ItemRefreshRowBinding.inflate(inflater, b.llRows, false).also { row ->
                    val isUrl = playlist.sourceType == PlaylistEntity.SOURCE_URL
                    row.ivIcon.setImageResource(if (isUrl) R.drawable.ic_link else R.drawable.ic_description)
                    row.tvName.text = playlist.name
                    row.tvMeta.setText(if (isUrl) R.string.refresh_meta_url else R.string.source_file)
                    if (index == playlists.lastIndex) {
                        (row.root.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 0
                    }
                    b.llRows.addView(row.root)
                    bindRow(row, RowState.WAITING)
                }
            }

            fun header(done: Boolean, step: Int, updated: Int, failed: Int) {
                val ctx = activity
                when {
                    !done -> {
                        b.tileIcon.setBackgroundResource(R.drawable.bg_tile_high_16)
                        b.ivIcon.setImageResource(R.drawable.ic_sync)
                        b.ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.color_accent_light))
                        b.tvTitle.setText(R.string.refresh_title_running)
                        b.tvSub.text = ctx.getString(R.string.refresh_sub_running, minOf(step + 1, playlists.size), playlists.size)
                    }
                    playlists.isEmpty() -> {
                        b.tileIcon.setBackgroundResource(R.drawable.bg_tile_success_16)
                        b.ivIcon.setImageResource(R.drawable.ic_check_circle)
                        b.ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.color_success))
                        b.tvTitle.setText(R.string.refresh_title_empty)
                        b.tvSub.setText(R.string.refresh_sub_empty)
                    }
                    failed == 0 -> {
                        b.tileIcon.setBackgroundResource(R.drawable.bg_tile_success_16)
                        b.ivIcon.setImageResource(R.drawable.ic_check_circle)
                        b.ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.color_success))
                        b.tvTitle.setText(R.string.refresh_title_done)
                        b.tvSub.text = ctx.resources.getQuantityString(R.plurals.refresh_sub_done, playlists.size, playlists.size)
                    }
                    else -> {
                        b.tileIcon.setBackgroundResource(R.drawable.bg_tile_error_16)
                        b.ivIcon.setImageResource(R.drawable.ic_error)
                        b.ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.color_error))
                        b.tvTitle.setText(R.string.refresh_title_issue)
                        b.tvSub.text = ctx.getString(R.string.refresh_sub_issue, updated, failed)
                    }
                }
            }

            b.btnAction.setText(R.string.action_cancel)
            header(done = playlists.isEmpty(), step = 0, updated = 0, failed = 0)
            var updated = 0
            var failed = 0
            playlists.forEachIndexed { index, playlist ->
                header(done = false, step = index, updated = updated, failed = failed)
                bindRow(rows[index], RowState.ACTIVE)
                val isUrl = playlist.sourceType == PlaylistEntity.SOURCE_URL
                val count = if (isUrl) {
                    activity.container.playlistRepository.refreshPlaylist(playlist.id)
                } else {
                    delay(FILE_ROW_PAUSE_MS)
                    playlist.channelCount
                }
                if (count == null) {
                    failed++
                    rows[index].tvMeta.setText(R.string.refresh_meta_failed)
                    bindRow(rows[index], RowState.FAILED)
                } else {
                    updated++
                    rows[index].tvMeta.text = activity.getString(
                        if (isUrl) R.string.refresh_meta_updated else R.string.refresh_meta_file,
                        count,
                    )
                    bindRow(rows[index], RowState.DONE)
                }
                b.progress.setProgress((index + 1) * 100 / playlists.size, true)
            }
            if (playlists.isEmpty()) b.progress.progress = 100
            header(done = true, step = playlists.size, updated = updated, failed = failed)
            finished = true
            b.btnAction.setText(R.string.action_done)
            b.btnAction.setBackgroundResource(R.drawable.bg_button_primary)
        }
    }

    private fun bindRow(row: ItemRefreshRowBinding, state: RowState) {
        row.ivDone.show(state == RowState.DONE)
        row.ivFailed.show(state == RowState.FAILED)
        row.ivSpinner.show(state == RowState.ACTIVE)
        row.tvWaiting.show(state == RowState.WAITING)
    }

    // --- Rate the app -------------------------------------------------------------------------

    fun rate(activity: AppCompatActivity, anchor: View) {
        val inflater = LayoutInflater.from(activity)
        val b = DialogRateBinding.inflate(inflater)
        val dialog = Dialogs.showCard(activity, b.root)
        val labels = activity.resources.getStringArray(R.array.rate_labels)
        val gold = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.color_warning))
        val off = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.color_star_off))
        var stars = 0

        fun refresh() {
            render(b, stars, labels, activity)
            renderStars(b, stars, gold, off)
        }

        for (k in 1..5) {
            val view = inflater.inflate(R.layout.item_rate_star, b.llStars, false) as ImageView
            run {
                if (k > 1) {
                    (view.layoutParams as ViewGroup.MarginLayoutParams).marginStart =
                        activity.resources.getDimensionPixelSize(R.dimen._4sdp)
                }
                view.contentDescription = activity.resources.getQuantityString(R.plurals.rate_star_description, k, k)
                view.setOnClickListener {
                    stars = k
                    refresh()
                }
                b.llStars.addView(view)
            }
        }

        refresh()

        b.btnNotNow.setOnClickListener { dialog.dismiss() }
        b.btnClose.setOnClickListener { dialog.dismiss() }
        b.btnSubmit.setOnClickListener {
            when {
                stars == 0 -> Unit
                stars >= 4 -> {
                    dialog.dismiss()
                    openStoreListing(activity, anchor)
                    Messages.show(anchor, R.string.toast_opening_store, R.drawable.ic_star, R.color.color_warning)
                }
                else -> {
                    val body = activity.getString(R.string.rate_feedback_body, stars) + "\n\n" + b.etFeedback.text.toString().trim()
                    val mail = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                        .putExtra(Intent.EXTRA_EMAIL, arrayOf(activity.getString(R.string.rate_feedback_email)))
                        .putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.rate_feedback_subject))
                        .putExtra(Intent.EXTRA_TEXT, body)
                    try {
                        activity.startActivity(mail)
                        b.layoutAsk.show(false)
                        b.layoutThanks.show(true)
                    } catch (_: ActivityNotFoundException) {
                        Messages.error(anchor, R.string.toast_no_app)
                    }
                }
            }
        }
    }

    private fun render(b: DialogRateBinding, stars: Int, labels: Array<String>, activity: Context) {
        b.tvRateLabel.text = labels[stars]
        b.tvRateLabel.setTextColor(
            ContextCompat.getColor(activity, if (stars > 0) R.color.color_warning else R.color.color_text_tertiary),
        )
        b.etFeedback.show(stars in 1..3)
        b.btnSubmit.setText(
            when {
                stars >= 4 -> R.string.rate_cta_store
                stars > 0 -> R.string.rate_cta_feedback
                else -> R.string.rate_submit
            },
        )
        b.btnSubmit.alpha = if (stars > 0) 1f else DISABLED_ALPHA
    }

    private fun renderStars(b: DialogRateBinding, stars: Int, gold: ColorStateList, off: ColorStateList) {
        for (k in 1..b.llStars.childCount) {
            val view = b.llStars.getChildAt(k - 1) as ImageView
            view.setImageResource(if (k <= stars) R.drawable.ic_star else R.drawable.ic_star_border)
            view.imageTintList = if (k <= stars) gold else off
        }
    }

    private fun openStoreListing(activity: AppCompatActivity, anchor: View) {
        val pkg = activity.packageName
        val targets = listOf(
            Uri.parse("market://details?id=$pkg"),
            Uri.parse("https://play.google.com/store/apps/details?id=$pkg"),
        )
        for (uri in targets) {
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
                return
            } catch (_: ActivityNotFoundException) {
                // try the next target
            }
        }
        Messages.error(anchor, R.string.toast_no_app)
    }

    // --- About --------------------------------------------------------------------------------

    fun about(activity: AppCompatActivity, anchor: View) {
        val b = DialogAboutBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialogs.showCard(activity, b.root)

        val info = runCatching { activity.packageManager.getPackageInfo(activity.packageName, 0) }.getOrNull()
        val versionName = info?.versionName.orEmpty()
        val build = info?.let { PackageInfoCompat.getLongVersionCode(it) } ?: 0L
        b.tvVersion.text = activity.getString(R.string.about_version, versionName, build)

        stat(b.rowPlaylists.root, R.string.about_playlists, "…")
        stat(b.rowChannels.root, R.string.about_channels, "…")
        stat(b.rowUpdates.root, R.string.about_updates, activity.getString(R.string.about_status_not_checked))
        ItemAboutStatBinding.bind(b.rowUpdates.root).tvValue.setTextColor(
            ContextCompat.getColor(activity, R.color.color_text_secondary),
        )

        activity.lifecycleScope.launch {
            val playlists = activity.container.playlistRepository.observePlaylists().first().size
            val channels = activity.container.channelRepository.observeChannelCount().first()
            ItemAboutStatBinding.bind(b.rowPlaylists.root).tvValue.text = playlists.toString()
            ItemAboutStatBinding.bind(b.rowChannels.root).tvValue.text = channels.toString()
        }

        b.btnClose.setOnClickListener { dialog.dismiss() }
        b.btnCheck.setOnClickListener {
            // The app has no update server of its own: the store listing is where updates are published.
            openStoreListing(activity, anchor)
        }
    }

    private fun stat(root: View, label: Int, value: String) {
        val row = ItemAboutStatBinding.bind(root)
        row.tvLabel.setText(label)
        row.tvValue.text = value
    }

    // --- Share --------------------------------------------------------------------------------

    fun share(activity: AppCompatActivity, anchor: View) {
        val b = SheetShareBinding.inflate(LayoutInflater.from(activity))
        val dialog = Sheets.show(activity, b.root)
        val link = "https://play.google.com/store/apps/details?id=${activity.packageName}"
        val message = activity.getString(R.string.share_message_text, link)
        b.tvLink.text = link.removePrefix("https://")

        val copyIcon = sizedIcon(activity, R.drawable.ic_content_copy)
        val doneIcon = sizedIcon(activity, R.drawable.ic_check)
        b.btnCopy.setCompoundDrawablesRelative(copyIcon, null, null, null)
        b.btnCopy.setOnClickListener {
            activity.getSystemService<ClipboardManager>()?.setPrimaryClip(ClipData.newPlainText("link", link))
            b.btnCopy.setText(R.string.share_copied)
            b.btnCopy.setCompoundDrawablesRelative(doneIcon, null, null, null)
            b.btnCopy.postDelayed({
                b.btnCopy.setText(R.string.share_copy_link)
                b.btnCopy.setCompoundDrawablesRelative(copyIcon, null, null, null)
            }, COPIED_RESET_MS)
        }

        fun target(row: ItemShareTargetBinding, icon: Int, label: Int, first: Boolean, action: () -> Unit) {
            row.ivIcon.setImageResource(icon)
            row.tvLabel.setText(label)
            if (!first) {
                (row.root.layoutParams as ViewGroup.MarginLayoutParams).marginStart =
                    activity.resources.getDimensionPixelSize(R.dimen._8sdp)
            }
            row.root.setOnClickListener {
                dialog.dismiss()
                action()
            }
        }

        fun send(intent: Intent) {
            try {
                activity.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                Messages.error(anchor, R.string.toast_no_app)
            }
        }

        fun chooser() = Intent.createChooser(
            Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, message),
            activity.getString(R.string.share_title),
        )

        target(b.targetMessages, R.drawable.ic_sms, R.string.share_messages, true) {
            send(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")).putExtra("sms_body", message))
        }
        target(b.targetEmail, R.drawable.ic_mail, R.string.share_email, false) {
            send(
                Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.brand_name))
                    .putExtra(Intent.EXTRA_TEXT, message),
            )
        }
        target(b.targetBluetooth, R.drawable.ic_bluetooth, R.string.share_bluetooth, false) {
            val bluetooth = Intent(Intent.ACTION_SEND).setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, message).setPackage("com.android.bluetooth")
            try {
                activity.startActivity(bluetooth)
            } catch (_: ActivityNotFoundException) {
                send(chooser())
            }
        }
        target(b.targetMore, R.drawable.ic_more_horiz, R.string.share_more, false) { send(chooser()) }
    }

    private fun sizedIcon(context: Context, res: Int) = ContextCompat.getDrawable(context, res)!!.mutate().also {
        val size = context.resources.getDimensionPixelSize(R.dimen._20sdp)
        it.setBounds(0, 0, size, size)
        it.setTint(ContextCompat.getColor(context, R.color.color_accent_light))
    }
}

package com.iptvplayer.xtreamiptv.myiptvpro.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.FragmentPlaylistsBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemCategoryChipBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.PlaylistItem
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Dialogs
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.PlaylistAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.PlaylistCallbacks
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.Sheets
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ViewToggle
import com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist.PlaylistDetailsActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.search.SearchActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.settings.SettingsActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.Messages
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.vmFactory
import kotlinx.coroutines.launch

/** "Playlist" tab: banner, source filter and the playlist cards. */
class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val main get() = requireActivity() as MainActivity

    private val viewModel: PlaylistsViewModel by activityViewModels {
        vmFactory {
            val c = requireContext().container
            PlaylistsViewModel(c.playlistRepository, c.preferences, c.defaultPlaylists)
        }
    }

    private val adapter by lazy {
        PlaylistAdapter(
            PlaylistCallbacks(
                onOpen = { startActivity(PlaylistDetailsActivity.intent(requireContext(), it.id)) },
                onEdit = ::editPlaylist,
                onDelete = ::confirmDelete,
            ),
        )
    }

    private lateinit var toggle: ViewToggle
    private var layoutManager: GridLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupChips()
        setupEmptyState()
        bindBannerCollage()
        binding.btnSearch.setOnClickListener { main.openSearch(SearchActivity.Scope.All) }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        toggle = ViewToggle(binding.viewToggle, listOf(ViewMode.GRID, ViewMode.LIST), viewModel::setViewMode)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui -> if (ui != null) render(ui) }
            }
        }
    }

    private fun setupList() {
        val span = resources.getInteger(R.integer.grid_span)
        val lm = GridLayoutManager(requireContext(), span)
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = if (adapter.mode == ViewMode.GRID) 1 else span
        }
        layoutManager = lm
        binding.rvPlaylists.layoutManager = lm
        binding.rvPlaylists.adapter = adapter
        binding.rvPlaylists.itemAnimator = null
    }

    private fun setupChips() {
        configureChip(binding.chipAll, R.drawable.ic_menu, R.string.filter_all, SourceFilter.ALL)
        configureChip(binding.chipUrl, R.drawable.ic_link, R.string.filter_url, SourceFilter.URL)
        configureChip(binding.chipFile, R.drawable.ic_description, R.string.filter_file, SourceFilter.FILE)
    }

    private fun configureChip(chip: ItemCategoryChipBinding, icon: Int, label: Int, filter: SourceFilter) {
        chip.ivIcon.setImageResource(icon)
        chip.tvLabel.setText(label)
        chip.root.setOnClickListener { viewModel.setFilter(filter) }
    }

    private fun setupEmptyState() {
        val e = binding.emptyAll
        e.ivEmptyIcon.setImageResource(R.drawable.ic_no_data)
        e.tvEmptyTitle.setText(R.string.empty_playlist_title)
        e.tvEmptyDesc.setText(R.string.empty_playlist_desc)
        e.btnEmptyPrimary.apply {
            setText(R.string.empty_playlist_cta)
            show(true)
            setOnClickListener { main.openAddPlaylist() }
        }
        e.rowEmptyShortcuts.show(true)
        e.btnShortcutUrl.setOnClickListener { main.openAddPlaylist(startOnFile = false) }
        e.btnShortcutFile.setOnClickListener { main.openAddPlaylist(startOnFile = true) }
    }

    private fun render(ui: PlaylistsUi) {
        val res = resources
        binding.tvBannerStats.text = getString(
            R.string.banner_stats,
            res.getQuantityString(R.plurals.playlist_count, ui.totalPlaylists, ui.totalPlaylists),
            res.getQuantityString(R.plurals.channel_count, ui.totalChannels, ui.totalChannels),
        )
        val hasAny = ui.totalPlaylists > 0
        binding.sectionHeader.show(hasAny)
        binding.filterRow.show(hasAny)
        binding.emptyAll.root.show(!hasAny && !ui.seeding)
        binding.viewSeeding.show(!hasAny && ui.seeding)
        binding.tvPlaylistCount.text = res.getQuantityString(R.plurals.playlist_count, ui.totalPlaylists, ui.totalPlaylists)

        renderChip(binding.chipAll, ui.totalPlaylists, ui.filter == SourceFilter.ALL)
        renderChip(binding.chipUrl, ui.urlCount, ui.filter == SourceFilter.URL)
        renderChip(binding.chipFile, ui.fileCount, ui.filter == SourceFilter.FILE)

        toggle.select(ui.viewMode)
        adapter.mode = ui.viewMode
        adapter.submitList(ui.playlists)
        binding.rvPlaylists.show(ui.playlists.isNotEmpty())

        val filterEmpty = hasAny && ui.playlists.isEmpty()
        binding.emptyFilter.show(filterEmpty)
        if (filterEmpty) {
            binding.tvEmptyFilterTitle.text = getString(
                R.string.empty_filter_title,
                getString(if (ui.filter == SourceFilter.URL) R.string.filter_name_url else R.string.filter_name_file),
            )
        }
    }

    private fun renderChip(chip: ItemCategoryChipBinding, count: Int, selected: Boolean) {
        chip.tvCount.text = count.toString()
        chip.root.isSelected = selected
    }

    /** 3×3 collage of real channel logos; empty cells show the glyph placeholder. */
    /** The banner background is the design's fixed photo set (static artwork, not channel data). */
    private fun bindBannerCollage() {
        val photos = intArrayOf(
            R.drawable.art_photo_1074, R.drawable.art_photo_1069, R.drawable.art_photo_1018,
            R.drawable.art_photo_292, R.drawable.art_photo_1043, R.drawable.art_photo_1025,
            R.drawable.art_photo_133, R.drawable.art_photo_1036, R.drawable.art_photo_1015,
        )
        val grid = GridLayout(requireContext()).apply { rowCount = 3; columnCount = 3 }
        val gap = resources.getDimensionPixelSize(R.dimen._2sdp)
        photos.forEachIndexed { index, photo ->
            val tile = ImageView(requireContext()).apply {
                setImageResource(photo)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setBackgroundResource(R.drawable.bg_photo_8)
                clipToOutline = true
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
            val params = GridLayout.LayoutParams(GridLayout.spec(index / 3, 1f), GridLayout.spec(index % 3, 1f)).apply {
                width = 0
                height = 0
                setMargins(gap, gap, gap, gap)
            }
            grid.addView(tile, params)
        }
        // Figma: linear-gradient(90deg, #141827 0%, #141827 34%, rgba(20,24,39,.78) 56%, rgba(20,24,39,.15) 100%)
        val solid = ContextCompat.getColor(requireContext(), R.color.color_surface)
        val mid = ContextCompat.getColor(requireContext(), R.color.color_banner_fade_mid)
        val end = ContextCompat.getColor(requireContext(), R.color.color_banner_fade_end)
        binding.bannerFade.background = PaintDrawable().apply {
            shape = RectShape()
            shaderFactory = object : ShapeDrawable.ShaderFactory() {
                override fun resize(width: Int, height: Int): Shader = LinearGradient(
                    0f, 0f, width.toFloat(), 0f,
                    intArrayOf(solid, solid, mid, end), floatArrayOf(0f, 0.34f, 0.56f, 1f), Shader.TileMode.CLAMP,
                )
            }
        }
        binding.bannerCollage.removeAllViews()
        binding.bannerCollage.addView(grid, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun editPlaylist(item: PlaylistItem) {
        Sheets.playlistEdit(requireContext(), item.name) { name ->
            viewModel.rename(item.id, name)
            Messages.show(main.messageAnchor, R.string.toast_playlist_renamed, above = main.messageAbove)
        }
    }

    private fun confirmDelete(item: PlaylistItem) {
        Dialogs.confirm(
            context = requireContext(),
            title = getString(R.string.dialog_delete_playlist_title),
            description = getString(R.string.dialog_delete_playlist_desc, item.name),
            confirmText = R.string.action_delete,
        ) {
            viewModel.delete(item.id)
            Messages.show(main.messageAnchor, R.string.toast_playlist_deleted, R.drawable.ic_delete, R.color.color_error, main.messageAbove)
        }
    }

    override fun onDestroyView() {
        binding.rvPlaylists.adapter = null
        layoutManager = null
        _binding = null
        super.onDestroyView()
    }
}

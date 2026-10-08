package com.iptvplayer.xtreamiptv.myiptvpro.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.FragmentChannelsBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemChannelsHeaderBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.CategoryChipAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelCallbacks
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ViewToggle
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.ui.search.SearchActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.relativeTime
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.vmFactory
import kotlinx.coroutines.launch

/** "Channel" tab: continue-watching hero, category chips and the channel list. */
class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    private val main get() = requireActivity() as MainActivity

    private val viewModel: ChannelsViewModel by activityViewModels {
        vmFactory {
            val c = requireContext().container
            ChannelsViewModel(c.channelRepository, c.playlistRepository, c.preferences)
        }
    }

    private val header = HeaderAdapter()
    private val adapter by lazy {
        ChannelAdapter(
            ChannelCallbacks(
                onOpen = ::open,
                onFavorite = { main.actions.toggleFavorite(it) },
                onMore = { main.actions.showActions(it) },
            ),
        )
    }

    private var lastUi: ChannelsUi? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val span = resources.getInteger(R.integer.grid_span)
        val lm = GridLayoutManager(requireContext(), span)
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                if (position == 0 || adapter.mode != ViewMode.GRID) span else 1
        }
        binding.rvChannels.layoutManager = lm
        binding.rvChannels.adapter = ConcatAdapter(header, adapter)
        binding.rvChannels.itemAnimator = null

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    if (ui != null) {
                        lastUi = ui
                        adapter.mode = ui.viewMode
                        adapter.submitList(ui.channels)
                        header.update(ui)
                    }
                }
            }
        }
    }

    private fun open(item: ChannelItem) {
        val selected = lastUi?.selected.orEmpty()
        val source = when {
            selected.isNotEmpty() -> PlayerSource.Category(selected)
            else -> PlayerSource.AllChannels
        }
        main.openPlayer(source, item)
    }

    override fun onDestroyView() {
        binding.rvChannels.adapter = null
        header.release()
        _binding = null
        super.onDestroyView()
    }

    /** The single header row (title, hero, chips, list title + toggle). */
    private inner class HeaderAdapter : RecyclerView.Adapter<HeaderAdapter.Holder>() {
        private var ui: ChannelsUi? = null
        private var holder: Holder? = null

        fun update(value: ChannelsUi) {
            ui = value
            val h = holder
            if (h != null) h.bind(value) else notifyItemChanged(0)
        }

        fun release() {
            holder = null
        }

        override fun getItemCount() = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Holder(ItemChannelsHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)).also { holder = it }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            ui?.let(holder::bind)
        }

        inner class Holder(private val b: ItemChannelsHeaderBinding) : RecyclerView.ViewHolder(b.root) {
            private val chips = CategoryChipAdapter { viewModel.select(it.value) }
            private val toggle = ViewToggle(
                b.viewToggle,
                listOf(ViewMode.CARDS, ViewMode.GRID, ViewMode.LIST),
                viewModel::setViewMode,
            )

            init {
                b.rvCategories.adapter = chips
                b.btnSearch.setOnClickListener { main.openSearch(searchScope()) }
                b.btnSeeAll.setOnClickListener { main.goToTab(MainActivity.Tab.RECENT) }
            }

            private fun searchScope(): SearchActivity.Scope {
                val selected = lastUi?.selected.orEmpty()
                return if (selected.isEmpty()) SearchActivity.Scope.All else SearchActivity.Scope.Category(selected)
            }

            fun bind(ui: ChannelsUi) {
                val ctx = b.root.context
                val res = ctx.resources
                b.tvSubtitle.text = ctx.getString(
                    R.string.channels_subtitle,
                    res.getQuantityString(R.plurals.channel_count, ui.totalChannels, ui.totalChannels),
                    res.getQuantityString(R.plurals.playlist_count, ui.playlistCount, ui.playlistCount),
                )

                val hero = ui.hero
                b.heroSection.show(hero != null)
                if (hero != null) {
                    b.heroThumb.bind(hero.logoUrl)
                    b.tvHeroName.text = hero.displayName
                    b.tvHeroCategories.text = hero.categoryLine
                    b.tvHeroQuality.text = hero.quality?.uppercase()
                    b.tvHeroQuality.show(hero.quality != null)
                    b.tvHeroWatched.text = hero.playedAt?.let { ctx.getString(R.string.watched_ago, ctx.relativeTime(it)) }
                    b.hero.setOnClickListener { main.openPlayer(PlayerSource.Recent, hero) }
                }

                val chipList = ui.chips.map { if (it.value.isEmpty()) it.copy(label = ctx.getString(R.string.category_all)) else it }
                chips.submitList(chipList)
                chips.selectedValue = ui.selected.takeIf { sel -> ui.chips.any { it.value == sel } } ?: ""

                b.tvListTitle.text = ui.selected.ifEmpty { ctx.getString(R.string.channels_list_all) }
                b.tvListCount.text = res.getQuantityString(R.plurals.channel_count, ui.channels.size, ui.channels.size)
                toggle.select(ui.viewMode)
                b.emptyCard.show(ui.channels.isEmpty())
            }
        }
    }
}

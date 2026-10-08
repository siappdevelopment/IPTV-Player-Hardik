package com.iptvplayer.xtreamiptv.myiptvpro.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.prefs.ViewMode
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.FragmentCollectionBinding
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelAdapter
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelCallbacks
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ViewToggle
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.ui.search.SearchActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.vmFactory
import kotlinx.coroutines.launch

/** Recent channels / Favorite channels. */
class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private val main get() = requireActivity() as MainActivity

    private val mode: CollectionMode by lazy {
        CollectionMode.valueOf(requireArguments().getString(ARG_MODE) ?: CollectionMode.RECENT.name)
    }

    private val viewModel: CollectionViewModel by viewModels {
        vmFactory {
            val c = requireContext().container
            CollectionViewModel(c.channelRepository, c.preferences, mode)
        }
    }

    private val adapter by lazy {
        ChannelAdapter(
            callbacks = ChannelCallbacks(
                onOpen = { main.openPlayer(if (mode == CollectionMode.RECENT) PlayerSource.Recent else PlayerSource.Favorites, it) },
                onFavorite = { main.actions.toggleFavorite(it) },
                onMore = { main.actions.showActions(it, inRecent = mode == CollectionMode.RECENT) },
            ),
            showWatched = mode == CollectionMode.RECENT,
        )
    }

    private lateinit var toggle: ViewToggle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recent = mode == CollectionMode.RECENT
        binding.tvTitle.setText(if (recent) R.string.recent_title else R.string.favorites_title)
        binding.btnSearch.setOnClickListener {
            main.openSearch(if (recent) SearchActivity.Scope.Recent else SearchActivity.Scope.Favorites)
        }

        val span = resources.getInteger(R.integer.grid_span)
        val lm = GridLayoutManager(requireContext(), span)
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = if (adapter.mode == ViewMode.GRID) 1 else span
        }
        binding.rvChannels.layoutManager = lm
        binding.rvChannels.adapter = adapter
        binding.rvChannels.itemAnimator = null
        toggle = ViewToggle(binding.viewToggle, listOf(ViewMode.GRID, ViewMode.LIST), viewModel::setViewMode)

        val empty = binding.emptyState
        empty.ivEmptyIcon.setImageResource(R.drawable.ic_no_data)
        empty.tvEmptyTitle.setText(if (recent) R.string.recent_empty_title else R.string.favorites_empty_title)
        empty.tvEmptyDesc.setText(if (recent) R.string.recent_empty_desc else R.string.favorites_empty_desc)
        empty.btnEmptyPill.apply {
            setText(if (recent) R.string.recent_empty_cta else R.string.favorites_empty_cta)
            show(true)
            setOnClickListener { main.goToTab(MainActivity.Tab.CHANNEL) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui -> if (ui != null) render(ui) }
            }
        }
    }

    private fun render(ui: CollectionUi) {
        val count = ui.items.size
        val has = count > 0
        binding.subRow.show(has)
        binding.emptyState.root.show(!has)
        binding.rvChannels.show(has)
        binding.tvSub.text = resources.getQuantityString(
            if (mode == CollectionMode.RECENT) R.plurals.channel_count else R.plurals.saved_channel_count,
            count, count,
        )
        toggle.select(ui.viewMode)
        adapter.mode = ui.viewMode
        adapter.submitList(ui.items)
    }

    override fun onDestroyView() {
        binding.rvChannels.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_MODE = "mode"

        fun newInstance(mode: CollectionMode) = CollectionFragment().apply {
            arguments = Bundle().apply { putString(ARG_MODE, mode.name) }
        }
    }
}

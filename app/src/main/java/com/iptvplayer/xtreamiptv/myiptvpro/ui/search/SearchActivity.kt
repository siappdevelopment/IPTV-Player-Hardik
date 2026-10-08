package com.iptvplayer.xtreamiptv.myiptvpro.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.iptvplayer.xtreamiptv.myiptvpro.R
import com.iptvplayer.xtreamiptv.myiptvpro.container
import com.iptvplayer.xtreamiptv.myiptvpro.data.repository.SearchScope
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ActivitySearchBinding
import com.iptvplayer.xtreamiptv.myiptvpro.databinding.ItemFormChipBinding
import com.iptvplayer.xtreamiptv.myiptvpro.domain.model.ChannelItem
import com.iptvplayer.xtreamiptv.myiptvpro.ui.common.ChannelActionHandler
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerActivity
import com.iptvplayer.xtreamiptv.myiptvpro.ui.player.PlayerSource
import com.iptvplayer.xtreamiptv.myiptvpro.ui.playlist.PlaylistDetailsActivity
import com.iptvplayer.xtreamiptv.myiptvpro.utils.applySystemBarInsets
import com.iptvplayer.xtreamiptv.myiptvpro.utils.enableAppEdgeToEdge
import com.iptvplayer.xtreamiptv.myiptvpro.utils.hideKeyboard
import com.iptvplayer.xtreamiptv.myiptvpro.utils.onTrimmedTextChanged
import com.iptvplayer.xtreamiptv.myiptvpro.utils.show
import com.iptvplayer.xtreamiptv.myiptvpro.utils.showKeyboard
import com.iptvplayer.xtreamiptv.myiptvpro.utils.vmFactory
import kotlinx.coroutines.launch

/** Scoped search: the whole library, one playlist, one category, Recent or Favorites. */
class SearchActivity : AppCompatActivity() {

    sealed interface Scope {
        data object All : Scope
        data class Playlist(val id: Long, val name: String) : Scope
        data class Category(val name: String) : Scope
        data object Recent : Scope
        data object Favorites : Scope
    }

    private lateinit var binding: ActivitySearchBinding
    private val scope: Scope by lazy { readScope(intent) }

    private val viewModel: SearchViewModel by viewModels {
        vmFactory {
            SearchViewModel(
                scope = when (val s = scope) {
                    Scope.All -> SearchScope.All
                    is Scope.Playlist -> SearchScope.Playlist(s.id, s.name)
                    is Scope.Category -> SearchScope.Category(s.name)
                    Scope.Recent -> SearchScope.Recent
                    Scope.Favorites -> SearchScope.Favorites
                },
                channels = container.channelRepository,
                playlists = container.playlistRepository,
            )
        }
    }

    private val actions by lazy {
        ChannelActionHandler(this, container.channelRepository, container.playlistRepository, { binding.root })
    }

    private val adapter by lazy {
        SearchAdapter(
            onChannel = ::openChannel,
            onFavorite = actions::toggleFavorite,
            onPlaylist = { startActivity(PlaylistDetailsActivity.intent(this, it.id)) },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableAppEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchRoot.applySystemBarInsets()

        binding.tvScope.text = getString(R.string.search_scope, scopeLabel())
        binding.btnBack.setOnClickListener { finish() }
        binding.btnClear.setOnClickListener { binding.etQuery.text?.clear() }
        binding.etQuery.onTrimmedTextChanged {
            binding.btnClear.show(it.isNotEmpty())
            viewModel.setQuery(it)
        }
        binding.etQuery.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.hideKeyboard()
                true
            } else {
                false
            }
        }
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter
        binding.rvResults.itemAnimator = null

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.ui.collect(::render) }
                launch { viewModel.suggestions.collect(::renderSuggestions) }
            }
        }
        if (savedInstanceState == null) binding.etQuery.post { binding.etQuery.showKeyboard() }
    }

    private fun render(ui: SearchUi) {
        binding.sectionIdle.show(ui is SearchUi.Idle)
        binding.rvResults.show(ui is SearchUi.Results)
        binding.sectionNoResults.show(ui is SearchUi.NoResults)
        when (ui) {
            is SearchUi.Results -> adapter.submitList(ui.rows)
            SearchUi.NoResults -> binding.tvNoResultsTitle.text =
                getString(R.string.search_no_results_title, binding.etQuery.text.toString().trim())
            SearchUi.Idle -> adapter.submitList(emptyList())
        }
    }

    private fun renderSuggestions(categories: List<String>) {
        binding.groupSuggestions.removeAllViews()
        categories.forEach { category ->
            val chip = ItemFormChipBinding.inflate(LayoutInflater.from(this), binding.groupSuggestions, false).root
            chip.text = category
            chip.setOnClickListener { binding.etQuery.setText(category); binding.etQuery.setSelection(category.length) }
            binding.groupSuggestions.addView(chip)
        }
        binding.groupSuggestions.show(categories.isNotEmpty())
    }

    private fun scopeLabel(): String = when (val s = scope) {
        Scope.All -> getString(R.string.search_scope_all)
        is Scope.Playlist -> s.name
        is Scope.Category -> s.name
        Scope.Recent -> getString(R.string.search_scope_recent)
        Scope.Favorites -> getString(R.string.search_scope_favorites)
    }

    private fun openChannel(item: ChannelItem) {
        val source = when (val s = scope) {
            is Scope.Playlist -> PlayerSource.Playlist(s.id, s.name)
            is Scope.Category -> PlayerSource.Category(s.name)
            Scope.Recent -> PlayerSource.Recent
            Scope.Favorites -> PlayerSource.Favorites
            Scope.All -> item.playlistId?.let { PlayerSource.Playlist(it, "") } ?: PlayerSource.Single
        }
        startActivity(PlayerActivity.intent(this, source, item))
    }

    companion object {
        private const val EXTRA_TYPE = "scope_type"
        private const val EXTRA_ID = "scope_id"
        private const val EXTRA_NAME = "scope_name"

        fun intent(context: Context, scope: Scope): Intent = Intent(context, SearchActivity::class.java).apply {
            when (scope) {
                Scope.All -> putExtra(EXTRA_TYPE, "all")
                is Scope.Playlist -> { putExtra(EXTRA_TYPE, "playlist"); putExtra(EXTRA_ID, scope.id); putExtra(EXTRA_NAME, scope.name) }
                is Scope.Category -> { putExtra(EXTRA_TYPE, "category"); putExtra(EXTRA_NAME, scope.name) }
                Scope.Recent -> putExtra(EXTRA_TYPE, "recent")
                Scope.Favorites -> putExtra(EXTRA_TYPE, "favorites")
            }
        }

        private fun readScope(intent: Intent): Scope = when (intent.getStringExtra(EXTRA_TYPE)) {
            "playlist" -> Scope.Playlist(intent.getLongExtra(EXTRA_ID, -1L), intent.getStringExtra(EXTRA_NAME).orEmpty())
            "category" -> Scope.Category(intent.getStringExtra(EXTRA_NAME).orEmpty())
            "recent" -> Scope.Recent
            "favorites" -> Scope.Favorites
            else -> Scope.All
        }
    }
}

# HTML design → native Android mapping

Source: `IPTV Player - All Screens.html` (Claude Design bundle; real design = `IPTV Player.dc.html`, 393×852 dp phone canvas,
16 screens/states). The HTML is **design reference only** – nothing from it runs in the app (no WebView, no HTML).
Design tokens, icons and copy were extracted from it; demo data (picsum photos, "Wildline Nature", ads) was ignored.

| HTML screen / component | Android XML | Kotlin (existing → change) | Data / logic reused |
|---|---|---|---|
| Splash | `activity_splash` | `SplashActivity` (restyled, routes by first-run flag) | `AppPreferences` (new) |
| Language | `activity_language`, `item_language` | `LanguageActivity` (new) | `AppPreferences`, `AppCompatDelegate` locales |
| Onboarding 01–04 | `activity_onboarding` (+4 illustration includes) | `OnboardingActivity` (new) | `AppPreferences` |
| Playlist Home (grid/list, chips, banner) | `fragment_playlists`, `item_playlist_card`, `item_playlist_row` | `PlaylistsFragment`/`PlaylistsViewModel` (reworked) | `PlaylistRepository`, Room `playlists`/`channels` |
| Channel Home (hero, chips, cards/grid/list) | `fragment_channels`, `item_channel_card`, `item_channel_grid`, `item_channel_row`, `item_category_chip` | `ChannelsFragment`/`ChannelsViewModel` (replaces `ChannelListFragment` ALL mode) | `ChannelRepository` |
| Recent Channels | `fragment_collection` | `CollectionFragment`(RECENT) | `recents` table |
| Favorite Channels | `fragment_collection` | `CollectionFragment`(FAVORITE) | `favorites` table |
| Bottom nav + centre "+" | `activity_main` | `MainActivity` | existing tabs |
| Add menu sheet | `sheet_add_menu` | `Sheets.showAddMenu` | – |
| Add Playlist (URL / File, errors, loading) | `activity_add_playlist`, `item_suggestion` | `AddPlaylistActivity`/`AddPlaylistViewModel` | `PlaylistRepository.import*`, `M3uParser` |
| Playlist channels | `activity_playlist_details` | `PlaylistDetailsActivity` (+VM) | `ChannelRepository` |
| Live Player (portrait / fullscreen / lock / loading / error) | `activity_player`, `layout-land/activity_player`, `view_player_overlay`, `item_player_channel` | `PlayerActivity`/`PlayerViewModel` (UI rebuilt, ExoPlayer logic kept) | Media3 |
| Search (scoped) | `activity_search`, `item_search_playlist` | `SearchActivity`/`SearchViewModel` (new) | new DAO search queries |
| Settings | `activity_settings`, `item_setting_row`, `item_setting_toggle` | `SettingsActivity` (rebuilt) | `AppPreferences`, repositories |
| Channel actions sheet | `sheet_channel_actions` | `Sheets.showChannelActions` | favorites / delete |
| Channel form sheet (add/edit) | `sheet_channel_form` | `Sheets.showChannelForm` | `ChannelRepository.addChannel/updateChannel` |
| Edit playlist sheet | `sheet_playlist_edit` | `Sheets.showPlaylistEdit` | `renamePlaylist` |
| Delete / clear dialogs | `dialog_confirm` | `Dialogs.confirm` | – |
| Toast | styled Snackbar | `Messages` helper | – |
| Native ads / banner ad / "Ad" chips | **omitted** | – | – |

## Tokens
Colors → `values/colors.xml`; radii/spacing → `dimens.xml`; gradient `#318CFF→#6246FF` (135°); icons are Material Rounded vectors
(Android Studio's bundled set) in `res/drawable/ic_*.xml`; filled/outline pairs via `sel_*.xml` selectors.
Font: the design uses Figtree; Android build uses the system sans-serif (Figtree TTF not bundled – see report).

## Conscious deviations
- Search matches channel **name or category** (design placeholder says so); R&D reference matched name only.
- Playlist edit sheet edits the name only (design); URL copy row from the old dialog was dropped.
- Player error uses the design's overlay with Retry/Prev/Next instead of the old Cancel/Next dialog.
- App language: choice is stored and applied with `AppCompatDelegate.setApplicationLocales`; translated string files are not bundled.

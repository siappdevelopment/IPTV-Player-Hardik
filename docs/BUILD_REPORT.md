# Build Report — IPTV Smart Player - Live TV

Source of truth: `docs/rnd/IPTV_Online_Player_Complete_RnD.md`. Ads intentionally excluded.

## Stack
Kotlin, XML + ViewBinding, MVVM + repositories, Room, Coroutines/StateFlow, RecyclerView + ListAdapter/DiffUtil,
Media3 ExoPlayer (HLS/DASH), Coil (logos). minSdk 24, targetSdk 37. No Compose, no ad SDKs.
Kotlin plugin is pinned to 2.3.21 in the root `build.gradle.kts` (AGP 9.5 alpha's built-in Kotlin is too old for current libraries).

## Screens
Splash, Main (tabs Playlist / Channel / Recent / Favorite + "+"), Add Playlist (Import Link / Upload File),
Playlist Details, Player (portrait + fullscreen landscape), Settings, Guides, dialogs (rename, confirm, player error).

## Database (Room, v1)
- `playlists(id, name, sourceType URL|FILE, source, createdAt)`
- `channels(id, playlistId FK CASCADE, name, groupTitle, logoUrl, streamUrl, tvgId, position)` — indexes: playlistId, groupTitle, streamUrl, name
- `favorites(streamUrl PK, addedAt)` — keyed by stream URL; orphans removed when a playlist/channel is deleted
- `recents(streamUrl PK, name, groupTitle, logoUrl, playlistId, playedAt)` — snapshot, no FK (survives playlist delete), capped at 200

## R&D checklist
| Item | Status |
|---|---|
| Launch (splash → main) | Done, device-tested |
| Playlist tab: search by name, All/URL/File filter, empty state, rename, delete(+confirm) | Done, tested (rename-save not exercised on device) |
| Add by https URL, suggestions, validation toasts, spinner | Done, tested (216-ch public playlist ≈1.8 s) |
| Add by file (SAF, real file name shown) | Done, tested |
| http:// rejected | Done, tested |
| M3U/M3U8 parser rules (R&D §9) | Done, 10 unit tests |
| Details: grid, search (name only), favorite, rename, delete, layered Back | Done, tested |
| Channel tab: category chips (`;` split), search | Done, tested |
| Recent: newest first, failed channels included, remove(+confirm), survives playlist delete | Done, tested |
| Favorite: once per URL, cascade on delete, empty state | Done, tested |
| Player: play/pause, prev/next, lock, favorite, Live badge, fullscreen, channel list, counter | Done, tested |
| Player error dialog, Next Channel drops failed channel for session only | Done, tested |
| Fullscreen ↔ portrait, Back from fullscreen returns portrait | Done, tested |
| Settings: Guides, Clear All Data, Rate, Share, Policy | Done; Clear tested; Rate/Share/Policy open system intents (not exercised) |
| Persistence after force-stop | Done, tested |
| Large lists (10,000 ch: import ≈5 s, smooth scroll, search) | Tested |
| Default-launcher gate, launcher shell, "Stream" option, ads | Intentionally NOT built (see R&D §33.1) |

## Tests
- Unit (JVM): 10 parser tests + template test — pass.
- Instrumented (on Samsung SM-M315F, Android 12): 5 Room rule tests + template — pass.
- Manual on device: flows listed above.
- Lint: 0 errors (warnings remain: splash-screen API suggestion, a few unused-layout hints).

## Known issues / assumptions
- Privacy policy URL is a placeholder (`privacy_policy_url` in strings.xml) — replace before release.
- Release APK is unsigned (`app-release-unsigned.apk`); signing config not set.
- Not tested: Android < 12 devices, tablets/TV/DPAD, offline playback, rotation on screens other than the player, 50,000-channel import.
- AGP is `9.5.0-alpha04` (as created by the template); a newer alpha exists.
- Orphaned recents (playlist deleted) open as a single-channel queue.

## Build status
Debug build: OK. Release build: OK (unsigned). Compilation: OK. Installation: OK on device. Runtime: no crashes observed.

## UI migration (Claude Design HTML → native XML) — final status

- All design screens are implemented as native XML + ViewBinding (no WebView / Compose / HTML at runtime): Splash, Language, Onboarding (4 pages), Main (Playlist / Channel / Recent / Favorite tabs + add menu), Add Playlist (URL / file), Playlist details, Search (scoped), Player (portrait + fullscreen, lock, error / retry), channel and playlist sheets, confirm dialog, Settings, Guides. Mapping: `DESIGN_MAPPING.md`.
- Existing data layer kept: Room DB, M3U parser, repositories, Media3 player. DAO signatures changed (search moved to repository `search(scope, q)`); `DatabaseRulesTest` updated.
- Verified: `assembleDebug`, `testDebugUnitTest`, `lintDebug` (0 errors), `compileDebugAndroidTest` pass. Not run: instrumented tests (`connectedAndroidTest`).
- Verified on device (SM phone, adb): splash → language → onboarding skip → home, file import, playlist details, search by category, favorites, recents ("Watched 1m ago"), playback (HLS), controls, fullscreen + Back exits fullscreen first, error overlay, settings, guides, confirm dialog.
- Known: Apple's bipbop CDN fails TLS ("trust anchor not found") on the test device network and shows the Network error overlay; other HTTPS streams play. Figtree font not bundled (system sans-serif). Language choice is stored/applied but no translations are bundled. Privacy URL is a placeholder. Release APK unsigned.

## SDP + exact typography pass (HTML re-export "All Screens", 51 screens)

- **Source of truth re-read**: the exported HTML now uses **Plus Jakarta Sans** (not Figtree) and has extra screens. Fonts embedded in the HTML as WOFF2 were converted to TTF with `tools/Woff2ToTtf.java` (Brotli + transformed glyf/loca/hmtx; variable `wght` axis kept) and bundled as `res/font/plus_jakarta_sans_variable.ttf`; `plus_jakarta_sans_{regular,medium,semibold,bold,extrabold}.xml` pin the weight (API 26+; API 24-25 fall back to the file's default instance). The design only renders weights 400 and 600.
- **SDP**: every UI dimension, text size, drawable radius/stroke/inset and Kotlin pixel value uses `@dimen/_Nsdp` (+ `_N_5sdp`, `_minusNsdp`). The scale set (`values*/sdp.xml`, generated by `tools/gen_sdp.ps1`) is calibrated to the design's 393 px viewport: 1 sdp = 1 HTML px at 393 dp smallest width, scaled per `swNNNdp`. No `dp`/`sp` literals remain in `res/` (except the SDP definitions and the launcher icon) or Kotlin. Lint `SpUsage`/`UnusedResources` are disabled on purpose.
- **Shadows**: CSS `box-shadow` is reproduced with stacked translucent layers (`tools/gen_shadows.ps1` → `shadow_*.xml`) on the bottom bar, "+" button, playlist tile and onboarding cards.
- **Icon + text buttons** use `IconTextView` so icon and label are centred together like the design's flex rows.
- **New screens**: Settings (restructured), language-from-Settings (Save), refresh-progress dialog (real per-playlist refresh), rate dialog (stars → Play Store / feedback e-mail → thanks), share sheet, about dialog, privacy policy. Removed (not in the design): "How to add a playlist" guide, "Clear all data".
- Build / unit tests / lint pass. Instrumented tests compile but were not run.

## Figma pass (file OiKUfRglIkbKDkvPsWJ4PG, 51 frames s01-s51)

- The Figma file is an HTML→Figma import of the same "All screens" prototype (same 51 screens, same 393×852 frames, same Plus Jakarta Sans tokens), so geometry/typography/colours were already taken from that source; Figma text-wrap differences (e.g. "Your playlists" breaking onto two lines, wrapped nav labels) are import artefacts and were NOT copied.
- The Figma MCP allowance of the plan ran out after the first Home (s07) design-context call, so only the assets listed in that response could be exported. Used from it: 17 SVG icons (search, settings, grid_view, view_list, menu, link, description, edit, delete, family_restroom, chevron_right, theaters, movie, sports_soccer, video_library, live_tv, history, favorite, add) → vector drawables via `tools/figma_svg_to_vd.ps1` (glyph centred in the design's font-size box), and the 9 photos for the onboarding illustrations (`drawable-nodpi/onb_photo_*.jpg`; the 10th, picsum 1080, wasn't in that response so the second "recent" row reuses 1015).
- Not replaced (no Figma export available): the remaining icons (arrow_back, close, player controls, check_circle, error, lock, star, share, etc.) still come from Android's Material Icons Round set.

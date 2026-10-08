# IPTV Online Player — Complete R&D Specification

> Evidence rule used throughout: **only behaviour observed through adb automation (UI hierarchy dumps, screenshots, `dumpsys`, logcat) is stated as fact.**
> Anything not observed is marked `Status: Not Verified`. Screenshots are in `screenshots/` (names referenced as `Sxx`).

---

## 1. Reference App

| Item | Value |
|---|---|
| App name | IPTV Online Player: Live TV |
| Package | `com.iptvplayer.streaming.watch.channels.smartiptv` (verified installed) |
| versionName / versionCode | `1.0` / `1` (from `dumpsys package`) |
| minSdk / targetSdk | 29 / 37 (as reported by `dumpsys package`) |
| Installed on device | 2026-10-05 (first/last update time identical) |
| Launcher activity | `.Activity.SplashActivity` |
| Other MAIN-filter activity | `.Activity.Launcher.LauncherMainActivity` |
| Activities observed at runtime | `SplashActivity`, `Permission.DefaultHomeActivity`, `Launcher.LauncherMainActivity`, `MainActivity` (not exported – `am start` denied), `AddPlaylist.AddPlaylistActivity`, `AddPlaylist.PlaylistDetailsActivity`, `AddPlaylist.PlayerActivity`, `SettingsActivity`, `HowToAddActivity` |
| Declared permissions (manifest via dumpsys) | INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE, FOREGROUND_SERVICE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, SCHEDULE_EXACT_ALARM, USE_FULL_SCREEN_INTENT, VIBRATE, WAKE_LOCK, REQUEST_DELETE_PACKAGES, SYSTEM_ALERT_WINDOW (not granted), CAMERA (not granted), RECORD_AUDIO (not granted) + ad-service permissions |
| Reference app modified? | No. Only normal in-app use + force-stop. Test data created by me was removed afterwards (see §32). |

## 2. Environment

| Item | Value |
|---|---|
| Device | Samsung SM-M315F, Android 12, 1080×2340 (density 420) |
| Connection | USB, `adb` (platform-tools), authorised |
| Tools used | `uiautomator dump` (UI hierarchy), `screencap`, `input tap/swipe/text/keyevent`, `dumpsys window/package/activity`, `logcat` |
| Unavailable / not used | Physical rotation (device auto-rotate is ON but I cannot turn the phone and did not change system settings), toggling Wi-Fi/airplane mode (system setting), device reboot, clean-install/Clear-Data (would destroy the installed app's existing data), network proxy/throttling |
| Test content | Own M3U file pushed to `/sdcard/Download`, public iptv-org playlist (via the app's own suggestion), public test HLS streams (Mux Big-Buck-Bunny, Apple bipbop), `example.com` |
| Automation noise | Interstitial/redirect ads fired on many navigations (opened Chrome/Brave/Play Store sheets). They were dismissed and are **not** documented (see §27). |

Limitations that affect confidence are listed in §32.

## 3. App Launch Flow

Observed cold-start sequence (force-stop then launch `SplashActivity`):

| # | Screen | Behaviour |
|---|---|---|
| 1 | `SplashActivity` | Branded splash with spinner, visible ≥5 s (seen at t≈2 s and t≈5 s) |
| 2 | (ad, ignored) | Full-screen ad with "Continue to app" |
| 3a | **First launch seen:** `DefaultHomeActivity` | "Set Default Home" gate (see below) |
| 3b | **Second cold start:** `LauncherMainActivity` directly | The gate did **not** reappear (n=2 launches; reason Not Verified) |
| 4 | `LauncherMainActivity` | Launcher-style shell home screen (see §6.0). Tap tile **"Downloader"** → `MainActivity` (the actual IPTV UI) |

### 3.1 `DefaultHomeActivity` (S02, S03, S04)
- Title **"Set Default Home"**, "FEATURES" card with 3 ticks ("Free multiple video downloads", "Free high-speed downloader", "Free secure & private tool"), two selectable rows (**IPTV Online Player: Live TV**, **System Launcher**), description "Premium Features Free Set Default App Now", primary button **TRY IT NOW FREE**, footer "You can change this anytime from Setting ➔ Apps ➔ Default Apps ➔ Home".
- **Back** → dialog **"Important Notice"** – "XNX HD Video Downloader needs to be set as your default home app to work properly." – single button **Setup Now**. (Copy references another app name → reused template text.)
- **Back again** → dialog dismissed, still on the gate.
- **System Launcher** row → opens Android's own *Default home app* chooser (`permissioncontroller … DefaultAppActivity`). Not changed during R&D (default home verified afterwards as `com.sec.android.app.launcher`).
- **TRY IT NOW FREE**: Not tested (would change a system setting).
- The gate can be bypassed by starting `LauncherMainActivity` (exported) → "Downloader" tile. Whether the gate is skippable by a normal user path: Not Verified.

## 4. Complete Screen Inventory

| ID | Screen | Entry point | Main function | Screenshot |
|---|---|---|---|---|
| A | Splash | Launch | Splash + loading | 01_launch_splash_ad_ignored (ad overlay only) |
| B | Default Home gate | Launch #1 | Ask to become default launcher | 02_default_home_gate, 03_default_home_notice_dialog, 04_system_home_app_chooser |
| C | Launcher-style home (`LauncherMainActivity`) | After A/B; Back from E | Shell home; tile "Downloader" opens E | 05_launcher_style_home |
| D | — (merged into E tabs) | | | |
| E | Main (`MainActivity`) – 4 tabs: Playlist / Channel / Recent / Favorite + FAB "+" | "Downloader" tile | Playlist list, all channels, history, favorites | 06_main_playlist_tab, 07_playlist_filter_file_empty, 23_channel_tab, 36_channel_tab_all, 24_recent_tab |
| F | Add Playlist (`AddPlaylistActivity`) title "Add Channel to Playlist" | "+" FAB or empty-state button | Import by URL / by file | 08, 09, 10, 11, 31, 32, 33, 34 |
| G | Playlist Details (`PlaylistDetailsActivity`) | Tap playlist card | Channel grid of one playlist + search | 13, 14, 17 |
| H | Player (`PlayerActivity`) | Tap channel | Video + channel list | 18–22 |
| I | Settings (`SettingsActivity`) | Gear on E | Guides, clear data, support links | 25, 28 |
| J | Guides (`HowToAddActivity`) | Settings → How to Add Playlist | Static how-to (tabs URL/File/Stream) | 26, 27 |
| Dlg | Dialogs: Rename Channel, Rename Playlist, Delete confirm, Remove from Recent, Clear All Data, player error | various | — | 15, 16, 28, 30, 35, 18 |
| Ext | System file picker, external Policy URL (Chrome) | F, I | — | 11 |

## 5. Navigation Map

(Full Mermaid diagram in §31.)

Summary: `Splash → [DefaultHome] → LauncherHome → (Downloader) → Main{Playlist|Channel|Recent|Favorite}`; `Main → Add`, `Main → Settings → Guides`, `Main(Playlist) → Details → Player`; Channel/Recent/Favorite cards also lead to Player (card tap → player: observed for Channel/Recent cards only as visible UI; tap not exercised, see §30).

## 6. Home Screen

### 6.0 `LauncherMainActivity` (S05) — launcher-style shell
Visible: wallpaper, day name, large clock, date (`08-Oct-2026`), three tiles (**Launcher Settings**, **Downloader** (IPTV logo), **Google** folder), web search bar "Search for web…" with Voice-search and News icons, page indicator "Search", dock of 5 app icons.
- **Downloader** tile → Main (verified). Others (Launcher Settings, Google folder, search, voice, news, dock icons): `Status: Not Verified` (not opened – dock icons launch phone apps).
- **Back** on this screen: no effect (stays).

### 6.1 Main screen – Playlist tab (S06)
- Top: search field "Search your channel" (`etSearch`), gear icon (`ivSettings`) → Settings.
- Static hero banner "IPTV Playlist – Steam, Explore & Enjoy" (decorative, not clickable-verified).
- Filter chips **All / URL / File** (`llAll`, `llUrl`, `llFile`). Selected chip has gradient fill.
- Playlist grid: 2 columns of cards. Each card: icon (category-specific icon for Family, Entertainment, Movies, Sports; a generic "playlist" icon for others), **pencil** (rename), **trash** (delete), name, `"N Channels"`, chevron. Whole card opens Details.
- **Empty state** (shown by File filter when no file playlists): illustration, "No Playlist found", "Create a new playlist by tapping the button below", button **"Add new Playlist +"** (S07).
- Bottom bar: **Playlist · Channel · [+ FAB] · Recent · Favorite**.
- Pre-existing data when R&D started: *Family 61, Entertainment 901, Movies 782, Sports 445* — all appear under the **URL** filter. Edit dialog showed Family = `https://iptv-org.github.io/iptv/categories/family.m3u`. Whether these are auto-seeded on first install: Not Verified.
- Filters: All = every playlist; URL = playlists imported from link; File = playlists imported from file (verified: file import appears only under File).
- Playlist order: creation order (new playlist appended at the end).
- **Search field on this tab** filters **playlists by name**, live, case-insensitive, partial (`FAM` → Family; `kzn` → nothing). No-match shows a blank list (no empty-state text). Back #1 closes the keyboard (text stays). Search on other tabs: Not Verified.

## 7. Playlist Management

| Operation | Verified | Detail |
|---|---|---|
| Add by URL | YES | §8 |
| Add by file | YES | §8 |
| Rename playlist | PARTIAL | Pencil → dialog **"Rename Playlist"**: editable name with clear (×), read-only **Link URL** with **Copy** button, **Cancel / Save Playlist** (S30). Saving not exercised. For file playlists the URL row content: Not Verified |
| Delete playlist | YES | Trash → dialog **"Delete Playlist" – "Are you sure you want to delete the Playlist ?"** [Cancel][Delete]; Delete removes immediately, no toast observed. **Cascade:** its channels' favorites disappeared; its **Recent** entries stayed (orphans) until removed manually |
| Refresh / re-download | NOT FOUND in UI | |
| Duplicate / reorder / favorite a playlist | NOT FOUND in UI | |
| Import progress | PARTIAL | Dim overlay + centered spinner, Add button dimmed; no percentage (S32) |
| Duplicate name/URL handling | Not Verified | |
| Clear all | See §21 | |

Flow template (URL import success):
Main `+` → Add screen → fill name+URL (or pick suggestion) → **Add Playlist +** → spinner → toast **"Playlist added successfully!"** → back to Main.

## 8. M3U/M3U8 Import

### 8.1 Add Playlist screen (`AddPlaylistActivity`, S08–S11)
- Toolbar: back arrow + "Add Channel to Playlist". Card with two tabs: **Import Link** | **Upload File**. The **Playlist Name** field value is shared across both tabs.
- **Import Link tab:** `Playlist Name *` (placeholder "Enter Playlist Name"), `Link URL *` (placeholder "www.//www.url_here.com"), each with a clear (×) button once non-empty; section **"Suggestion"** with 4 radio-style rows (Comedy, Lifestyle, Movies, Sports; each shows `https://iptv-org/<name>.m3u`). Selecting one fills Name and the **real** URL (verified for Comedy → `https://iptv-org.github.io/iptv/categories/comedy.m3u`) and shows a check mark. Primary button **Add Playlist +**.
- **Upload File tab:** `Playlist Name *`, "Upload File" label, button **Choose M3U File** → system document picker (not filtered to M3U – all files listed). After picking, the button label shows the raw document id (e.g. `msf:1000041738`), **not the file name**.
- Back arrow (toolbar `ivBack`) returns to Main; Back with keyboard open first closes the keyboard.

### 8.2 Validation & errors (observed)
| Input | Result |
|---|---|
| Both empty | Toast **"Please enter URL"** |
| Name only | Toast **"Please enter URL"** |
| URL only, invalid text (`abc`), no name | Toast **"Error loading playlist"** (stays on form) |
| `http://` URL (local PC, valid M3U), with name | Toast **"Error loading playlist"**. Logcat tag `IPTV`: `java.io.IOException: Cleartext HTTP traffic to 127.0.0.1 not permitted` → **cleartext HTTP is blocked; https only** |
| `https://example.com/nope.m3u` (HTTP 404) | Toast **"Error loading playlist"**, form values retained |
| `https://example.com/` (HTTP 200, HTML, not M3U) | **Accepted**: toast "Playlist added successfully!", playlist created with **1 channel**: blank name, category "Unknown" (S34). **No content validation** |
| File tab, no file chosen | Toast **"Please choose a file"** |
| Valid https M3U (Comedy, 216 channels) | Success; ~6 s total including an interstitial |
| Valid file (10 EXTINF) | Success toast, returns to Main with **File** chip selected |
| Valid URL with empty name | Not Verified (masked by the http failure) |
| Other-host/timeouts/403/slow server | Not Verified |

Toasts last ≈2 s (Android default).

## 9. Playlist Parser Behavior

Test file (10 `#EXTINF` entries) → app showed **9 channels**. Observed rules:

| Case | Observed |
|---|---|
| `#EXTM3U` header | Accepted |
| Channel **display name** | `tvg-name` when present, otherwise text after the last comma (e.g. `tvg-name="Mux Test"` + title "Mux Test Stream" → **"Mux Test"**; no tvg-name → "Dead Stream 404") |
| **Category** | `group-title`; missing → **"Unknown"**; value with `;` kept as a single raw string (e.g. `Entertainment;Family;General` shown verbatim on Channel tab cards) |
| Stream URL | Stored as-is; shown read-only in Rename Channel dialog; invalid scheme `notaurl://foo` was **kept** as a channel (fails at play time) |
| Duplicate channels (same name+URL) | **Both kept** |
| Empty channel name (`,` with nothing) | Kept with **blank name** (card shows only category) |
| Unicode names (`ÜñíCödé Chännel 日本`) | Displayed correctly |
| `#EXTINF` immediately followed by another `#EXTINF` (no URL line) | The first entry (**Delta / "Malformed no url line"**) was **dropped** |
| Non-M3U text (HTML) | Becomes one blank-name channel, category Unknown |
| `tvg-logo` | Logos **are** displayed on Channel/Recent cards for pre-existing playlists (e.g. 1KZN TV, Asia TV). In the Playlist Details grid a **generic TV icon** is always used (observed for my channels and the channel list). Whether my own `tvg-logo` (Wikimedia PNG) loads on the Channel tab: Not Verified |
| `tvg-id`, other EXTINF attributes | Not visible anywhere in UI; usage Not Verified |
| Empty groups | N/A (groups only exist via channels) |

## 10. Categories

- No separate category screen. Category = channel's group string.
- **Channel tab** shows a horizontal chip row: **All**, then categories (observed *Entertainment*, *Family* …, row scrolls; tapping a chip highlights it and the first card remained 1KZN TV – full filter result/exact chip derivation: PARTIAL). Chip icons are category-specific for known names.
- Chips split the raw `a;b;c` value (card shows full raw string, chips show single names) – PARTIAL (not exhaustively checked).

## 11. Channel List

### 11.1 Playlist Details (`PlaylistDetailsActivity`, S13, S14)
- Toolbar: back arrow, **playlist name**, **search** icon.
- 2-column grid, order = file order. Card: generic TV icon, **♡ favorite**, **✎ rename**, **🗑 delete**, name (2 lines max, ellipsized), category (single line), chevron. Entire card opens the player.
- Native-ad rows are inserted between channel rows (ignored).
- **♡**: toggles filled immediately, no toast. Favorites are keyed by **stream URL** — favouriting one of two duplicate channels filled both hearts and the Favorite tab shows **one** entry.
- **✎** → dialog **"Rename Channel"** (S15): editable name (× clear), read-only "Link URL" (+ Copy button), **Cancel / Save**. Save updates the card in place immediately. Hardware Back closes the dialog without saving.
- **🗑** → dialog "Delete Playlist" text (S16) [Cancel][Delete] — **same wording as playlist delete** even for a channel. Delete removes the card immediately and persists (count dropped 9 → 8 after restart).
- Long-press on card: Not Verified (not tested). Sorting/filtering UI: none found.
- **Large list:** 901-channel playlist opens and scrolls smoothly (no pagination indicator; lazy list). See §25.

### 11.2 Channel tab (Main, S23)
Title "Channel", search icon (right), chip row, native ad, then **large cards** (logo big, ♡ ✎ 🗑 overlay top-right, category text in orange, name, **"Explore ›"** button). Lists channels across playlists. Tap/Explore → player (not exercised, Not Verified). Search on this tab: Not Verified.

## 12. Search

| Location | Verified behaviour |
|---|---|
| Main › Playlist tab search field | Live filter of **playlist names**; case-insensitive; partial; blank list if none; Back #1 hides keyboard |
| Playlist Details search (S17) | Tap magnifier → toolbar replaced by `‹` + field "Search.." + clear ×. Live filter on **channel name only**, case-insensitive, **partial** (`BIP` → both BipBop; `mux` → renamed Mux). **Does not** match category (`alpha`, `beta`, `unknown`) or URL (`https`). No results → **blank grid, no empty-state text**. × clears text. Back #1 closes keyboard, Back #2 closes search bar and restores title |
| Channel / Recent / Favorite search icon | Not Verified |
| Global search across playlists | Not found |

Results appear within ≈0.5 s on a 901-channel playlist.

## 13. Favorites

- Add: ♡ on any channel card (Details, Channel, Recent) or in the player (see §16); instant, no toast.
- Remove: tap the filled ♡ — Not Verified separately (state toggling observed one-way in tests; the control is a toggle by icon state).
- **Favorite tab:** shows favorited channels once per stream URL. Empty state texts: **"No channels Favourite"** / **"Tap the favorite icon to add to your favorite."** When populated, the header text still read **"Channel"** (observed).
- Persistence: favorites survived force-stop + cold start (verified).
- Deleting the owning playlist removes its favorites (verified).
- Play a favorite / search favorites / category behaviour: Not Verified.

## 14. Recent/History

Feature: **Found** (tab **Recent**, header "Recent Channels", search icon).
- Grid cards like Channel list (♡ ✎ 🗑), logo when available.
- Order: most-recently opened first. A channel that **failed** to play was still added (BipBop, Dead Stream). Playing "MuxRenamed" did **not** produce an entry in my run (reason Not Verified – possible ad-flow interference).
- Pre-existing entries (1KZN TV, AMG TV, Canal IPe, 6 Wise Tv…) show history existed before R&D.
- 🗑 → dialog **"Remove from Recent" – "Are you sure you want to remove this channel from recent list?"** [Cancel][Remove] (S35); Remove deletes only that entry.
- Persistence: survived force-stop; survives deletion of the source playlist (orphan entries remain).
- Max size / duplicate collapsing / "clear all history" button: Not Verified (Settings → Clear All Data mentions "watch history").

## 15. Media Player (`PlayerActivity`, S18–S22)

Portrait layout (top→bottom):
1. Toolbar: back arrow, playlist name, **"n/total Channel"** counter (e.g. `2/8 Channel`).
2. Video area (16:9, black letter-box) with overlay controls and centered loading spinner.
3. Scrollable **channel list of the same playlist** (rows: TV icon, name, category, ♡; current row highlighted; native ads interleaved). Tapping a row presumably switches channel — Not Verified.

Streams tested: Mux HLS (`x36xhzz.m3u8`) **played**; Apple bipbop fMP4 HLS (`img_bipbop_adv_example_fmp4`) **showed the error dialog** (cause Not Verified – stream is valid in other players); example.com 404 URL → error dialog within ≈4 s; invalid scheme not exercised.

## 16. Player Controls

Controls overlay appears on tapping the video and **auto-hides after a few seconds** (exact timeout Not Measured). The video view's accessibility label toggles "Show/Hide player controls".

| Control | Default / behaviour |
|---|---|
| ♡ Favorite (top-right of video) | Present; toggle effect in player: Not Verified (coordinates hit while controls toggling; list heart not changed in my run) |
| 🔒 Lock (left-middle) | Lock hides **all other controls**, leaving only the lock icon; tapping video does not reveal others. Unlock worked after tapping video to reveal the lock icon, then the lock icon (needed more than one attempt – flaky UX). Long-press: no effect |
| ⏮ Previous / ⏭ Next | Present. Next verified: moves to next channel (counter increments; Dead Stream produced the error dialog) |
| Play/Pause (center) | Verified: pause shows ▶ icon (resource id `exo_play`), video freezes; tap again resumes |
| "● Live" badge (bottom-left) | **Always shown**, even for a VOD stream (Mux sample is a movie); no seek bar, no time/duration |
| Fullscreen ⛶ (bottom-right) | Verified (see §17) |
| Seek bar / progress, volume, mute, audio-track, subtitle, quality, speed, PiP, cast | **Not found** in the control overlay hierarchy. Gesture controls (brightness/volume swipe, double-tap seek): Not Verified |

Auto-hide / landscape / portrait differences: controls identical in both (landscape hides ♡ and ⛶ from hierarchy).

## 17. Fullscreen

- ⛶ forces **landscape** (display rotation changed 1080×2340 → 2340×1080), video fills the whole screen (cutout inset kept), toolbar & list hidden. Controls remain: lock, prev, play/pause, next, Live badge.
- **Back in fullscreen** returned to the **Playlist Details screen (portrait)**, i.e. left the player entirely (observed once; an interstitial could have been involved).
- There is no visible "exit fullscreen" button in the hierarchy (⛶ and ♡ have empty bounds in landscape).

## 18. Orientation

- Verified: only the forced landscape of the fullscreen player.
- Rotation of other screens, rotation during buffering/before playback, auto-rotate behaviour: `Status: Not Verified – Reason: device auto-rotate is on but physical rotation/setting changes are not possible with the available automation.`

## 19. Error Handling

| Situation | Behaviour |
|---|---|
| Player cannot open stream (404, unsupported/failed HLS) | Dialog (white card, ⓧ icon): **"This channel is currently unavailable in your region. Please try again later."** Buttons **Cancel** / **Next Channel** (S18). Same text for all failures seen – generic |
| **Cancel** | Closes player and returns to Details (after an ad) |
| **Next Channel** | Skips to next channel and **removes the failed channel from the in-session player list** (counter 2/8 → 3/7). Not persisted – the channel is still in the saved playlist |
| App crash | None observed in the whole session |
| Import errors | §8.2 |
| Retry button | Not found |
| 403 / timeout / network lost / empty response during playback | Not Verified |

## 20. Network Behavior

| Item | Result |
|---|---|
| Cleartext HTTP | **Blocked** for playlist import (`IOException: Cleartext HTTP traffic … not permitted`). Playback of `http://` streams: Not Verified |
| HTTPS import | Works; fetch via platform HTTP (okhttp internal); no auth |
| Logo loading | Works online on Channel/Recent cards |
| Offline behaviour (open, saved playlists, search, import, playback) | `Status: Not Verified – Reason: toggling Wi-Fi/airplane mode is a system setting` |

## 21. Settings (`SettingsActivity`, S25)

Header "Settings" with back arrow. No toggles, no preferences, no persisted options.

| Section | Item | Behaviour |
|---|---|---|
| General | **How to Add Playlist** | → `HowToAddActivity` "Guides" (§21.1) (an ad interstitial preceded it) |
| General | **Data Management – "Clear All Data"** (red label) | Standard AlertDialog **"Clear All Data" – "Are you sure you want to clear all playlists and watch history?"** [CANCEL][CLEAR] (S28). **CLEAR not executed** (would destroy user data) → result Not Verified |
| Sharing & Support | **Rate us** | Not Verified (external) |
| Sharing & Support | **Share app** | Not Verified (share sheet) |
| Sharing & Support | **Policy** | Opens an external web page in Chrome (`privacyplaylistsresponsivepolicy.blogspot.com/2026/08/…`); Back returns to Settings |

### 21.1 Guides screen (S26, S27)
Toolbar "Guides"; tab pills **URL | File | Stream**; static numbered steps with a mock form:
- URL: 1 Search for public iptv playlist… 2 Copy the M3U playlist link… 3 Go back, press **+**, select import playlist URL and paste → mock form (name "Zommit", `https://zoommit.m3u`) → 4 "Enjoy IPTV show!".
- File: Search M3U, download file, press **+** → Upload M3U file → mock (`Playlist2.m3u`).
- Stream: "press **+** then select **Stream** and paste the link"; text "Supported: YouTube, Tiktok, Video, mp4, m3u8, and standard protocols like http, https, rtsp,…"; mock buttons **Play Now** / **Add Video/Stream +**.
- **Important:** the real Add screen has **no "Stream" option** (only Import Link / Upload File), so "Stream" is documented but **not present as a function** in this build (verified from Main "+" on Playlist and Recent tabs).

## 22. Smart TV / Large Screen

- Test device is a phone. No Android TV/leanback device available; DPAD/focus navigation: `Status: Not Verified`.
- Chromecast / casting / external display: no cast UI found in player controls; `Status: Not Verified` beyond that.
- Landscape large-screen layout: only the fullscreen player.

## 23. Data Persistence

Tested with `force-stop` + cold start:

| Data | Persisted |
|---|---|
| Playlists | YES |
| Channels (incl. rename/delete edits) | YES (9 → 8 after delete, survived restart) |
| Favorites | YES |
| Recent | YES |
| Settings | N/A (none) |
| Last screen | NO (cold start goes Splash → launcher shell) |
| Last played channel | NO (no resume observed) |
| Search text | Not persisted (not observed) |
| Backgrounding/return (via ad detours to other apps) | State preserved (screens returned as left) |
| Device reboot | Not Verified |
| Rotation | Not Verified |

## 24. Back Navigation

| Screen / state | Back result (observed) |
|---|---|
| Default Home gate | Dialog "Important Notice"; Back again → dialog closes |
| Launcher-style home | No effect |
| Main (from Recent tab) | → Launcher-style home (tab-first behaviour for other tabs: Not Verified) |
| Main, keyboard open in search | Closes keyboard first |
| Add Playlist, keyboard open | Closes keyboard first; toolbar arrow → Main |
| Add Playlist during import | Not Verified |
| Playlist Details | toolbar arrow → Main (hardware Back: same expected, Not Verified) |
| Details, search open w/ keyboard | #1 closes keyboard, #2 closes search bar |
| Rename/Delete/Remove dialogs | Back closes dialog |
| Player (portrait) | Not Verified directly (toolbar arrow ≈ back to Details) |
| Player fullscreen | Left player → Details portrait |
| Player error dialog | Cancel → Details |
| Settings | → Main (an interstitial may appear) |
| Guides | → Settings |
| Chrome (Policy) | → Settings |

## 25. Performance

| Test | Result |
|---|---|
| Open 901-channel playlist | Opens, no crash/freeze observed (timing polluted by an interstitial) |
| Scroll 901-channel grid (10 fast flings via adb) | 2.8 s wall-clock for the 10 swipes; no jank/ANR observed |
| In-playlist search "news" on 901 channels | Results visible ≈0.5 s after typing |
| Import 216-channel playlist (Comedy, https) | ≈6 s end-to-end including an interstitial (parsing time itself Not Measured) |
| Player start (Mux HLS) | Video visible ≈3 s after tap (includes interstitial/ad handling) |
| Memory / CPU | Not Measured |

## 26. Permissions

- No runtime permission dialog appeared at any point (CAMERA, RECORD_AUDIO, SYSTEM_ALERT_WINDOW declared but not granted/requested in observed flows; notification permission prompt not observed).
- File import uses the system picker (no storage permission needed).
- Special access requested by the app: **Default home app (role)** via the gate (§3.1).

## 27. Ads — Ignored

```text
Ads:
Ignored for R&D and implementation.
```

(Ads were dismissed whenever they blocked automation; nothing about them is part of this specification.)

## 28. Complete Start-to-Close Flow

1. Launch → Splash (spinner) → (ad dismissed) → [first launch: Default Home gate → Back/Setup dialog loop; bypass via launcher activity] → **Launcher-style home**.
2. Tap **Downloader** → **Main / Playlist tab** (existing playlists or empty state).
3. Tap **+** → **Add** → choose **Import Link** (name + https URL, or a suggestion) or **Upload File** (name + system picker) → **Add Playlist +** → spinner → toast "Playlist added successfully!" → Main.
4. Tap a playlist → **Details** (grid) → optional: search, ♡, ✎ rename, 🗑 delete.
5. Tap a channel → **Player**: video + controls (♡, lock, prev, play/pause, next, Live, fullscreen) + channel list. Failure → error dialog (Cancel / Next Channel).
6. Fullscreen → landscape; Back → leaves to Details.
7. Back to Main → **Channel / Recent / Favorite** tabs; Recent updates automatically.
8. Gear → **Settings** → Guides / Clear All Data (dialog) / Rate / Share / Policy.
9. Close: Back on Main → launcher-style home; Back there does nothing; app is left via system Home/recents. Cold start shows playlists/favorites/recents intact.

## 29. Automated Test Matrix

| # | Test | Result |
|---|---|---|
| T1 | Package verification | PASS |
| T2 | Cold start trace (2 launches) | PASS (flow differs between launches) |
| T3 | Default-home gate Back behaviour | PASS |
| T4 | Filters All/URL/File | PASS |
| T5 | Add: empty / name-only / URL-only validation | PASS |
| T6 | Add by http URL | FAIL-by-design (cleartext blocked) – documented |
| T7 | Add by file (10-entry M3U) | PASS |
| T8 | Add by https (suggestion, 216 ch.) | PASS |
| T9 | Add https 404 | PASS (error toast) |
| T10 | Add HTML as playlist | PASS (accepted; lenient) |
| T11 | Parser matrix (§9) | PASS |
| T12 | Channel favorite toggle | PASS |
| T13 | Channel rename / delete | PASS |
| T14 | Details search (name/category/URL/none) | PASS |
| T15 | Player play, pause/resume | PASS |
| T16 | Player lock/unlock | PARTIAL (works, flaky) |
| T17 | Player next / error dialog / Next Channel / Cancel | PASS |
| T18 | Fullscreen + Back | PASS (n=1) |
| T19 | Channel tab chips | PARTIAL |
| T20 | Recent tab, remove entry | PASS |
| T21 | Favorite tab populated/empty | PASS |
| T22 | Persistence (force-stop) | PASS |
| T23 | Playlist rename dialog open | PASS (save not exercised) |
| T24 | Playlist delete (+cascade) | PASS |
| T25 | Settings items | PARTIAL |
| T26 | Guides tabs | PASS |
| T27 | Large-list scroll/search | PASS |
| T28 | Rotation, offline, reboot, TV/DPAD, cast | NOT TESTABLE |

## 30. Feature Inventory

| ID | Feature | Screen | Trigger | Expected Result | Verified |
|---|---|---|---|---|---|
| F01 | Splash | Splash | Launch | Splash then next screen | YES |
| F02 | Default-home gate | Gate | First launch | Ask for default home | YES (n=1) |
| F03 | Launcher-style shell | LauncherHome | After splash | Home with Downloader tile | YES |
| F04 | Open IPTV UI | LauncherHome | Tap Downloader | MainActivity | YES |
| F05 | Playlist grid | Main | Open | Cards w/ counts | YES |
| F06 | Filter All/URL/File | Main | Chip | Filter by source type | YES |
| F07 | Empty state | Main (File) | No playlists | Message + button | YES |
| F08 | Playlist search | Main | Type | Filter by name | YES |
| F09 | Add by URL | Add | Add Playlist+ | Playlist created (https) | YES |
| F10 | Add by file | Add | Pick file | Playlist created | YES |
| F11 | Suggestions | Add | Tap row | Fills name + URL | YES |
| F12 | Validation toasts | Add | Submit | Toasts | YES |
| F13 | Import spinner | Add | Submit | Overlay spinner | YES |
| F14 | Rename playlist | Main | ✎ | Dialog | PARTIAL |
| F15 | Delete playlist | Main | 🗑 | Confirm + delete | YES |
| F16 | Refresh/duplicate/reorder playlist | — | — | — | NO (not found) |
| F17 | Channel grid | Details | Open | Cards | YES |
| F18 | Channel favorite | Details | ♡ | Toggle | YES |
| F19 | Channel rename | Details | ✎ | Dialog + save | YES |
| F20 | Channel delete | Details | 🗑 | Confirm + delete | YES |
| F21 | Details search | Details | Magnifier | Live filter (name) | YES |
| F22 | Channel tab (all channels, chips) | Main | Tab | List + chips | PARTIAL |
| F23 | Recent tab + remove | Main | Tab/🗑 | List/dialog | YES |
| F24 | Favorite tab + empty state | Main | Tab | List/empty | YES |
| F25 | Player playback | Player | Tap channel | Video plays | YES |
| F26 | Play/Pause | Player | Tap | Toggle | YES |
| F27 | Prev/Next | Player | Tap | Switch | PARTIAL (Next only) |
| F28 | Lock | Player | Tap | Hide controls | PARTIAL |
| F29 | Fullscreen | Player | ⛶ | Landscape | YES |
| F30 | Player error dialog | Player | Fail | Cancel/Next Channel | YES |
| F31 | Seek/volume/subtitle/audio/quality/speed/PiP | Player | — | — | NO (not found) |
| F32 | Cast / Chromecast | Player | — | — | NOT TESTABLE |
| F33 | Settings: How to Add | Settings | Tap | Guides | YES |
| F34 | Settings: Clear All Data | Settings | Tap | Dialog | PARTIAL (not confirmed) |
| F35 | Settings: Rate / Share | Settings | Tap | External | NOT TESTABLE (not run) |
| F36 | Settings: Policy | Settings | Tap | External web page | YES |
| F37 | Persistence (playlists/fav/recent) | App | Restart | Retained | YES |
| F38 | Last-screen / last-channel restore | App | Restart | — | NO (not restored) |
| F39 | Offline behaviour | App | No network | — | NOT TESTABLE |
| F40 | Rotation (non-fullscreen) | App | Rotate | — | NOT TESTABLE |
| F41 | TV / DPAD | App | — | — | NOT TESTABLE |
| F42 | "Stream" add option (as in Guides) | Add | + | — | NO (not found) |
| F43 | Channel-tab search, Recent/Favorite search | Main | Magnifier | — | NOT TESTABLE (not run) |
| F44 | Card long-press menus | Cards | Long-press | — | NOT TESTABLE (not run) |

## 31. Navigation Diagram

```mermaid
flowchart TD
    A[Splash] --> AD[Ad - ignored]
    AD --> B{First launch?}
    B -- observed 1st --> G[Default Home gate]
    G -- Back --> GN[Important Notice dialog]
    GN -- Back --> G
    G -- System Launcher --> SYS[Android Default home chooser]
    G -- bypass via LauncherMainActivity --> L
    B -- observed 2nd --> L[Launcher-style home]
    L -- Downloader tile --> M
    L -- Back --> L
    subgraph Main[MainActivity]
      M[Playlist tab] <--> CH[Channel tab]
      M <--> RE[Recent tab]
      M <--> FA[Favorite tab]
    end
    M -- Back --> L
    M -- + FAB / Add new Playlist --> ADD[Add Playlist: Import Link | Upload File]
    CH -- + --> ADD
    RE -- + --> ADD
    ADD -- Upload File: Choose M3U --> PICK[System file picker]
    PICK --> ADD
    ADD -- success toast --> M
    ADD -- error toast --> ADD
    M -- pencil --> RP[Rename Playlist dialog]
    M -- trash --> DP[Delete confirm dialog]
    M -- tap card --> D[Playlist Details grid]
    D -- search icon --> DS[Inline search bar]
    D -- heart --> D
    D -- pencil --> RC[Rename Channel dialog]
    D -- trash --> DC[Delete confirm dialog]
    D -- tap card --> P[Player]
    CH -- tap card --> P
    RE -- tap card --> P
    FA -- tap card --> P
    RE -- trash --> RR[Remove from Recent dialog]
    P -- stream fails --> PE[Error dialog]
    PE -- Cancel --> D
    PE -- Next Channel --> P
    P -- fullscreen button --> PF[Fullscreen landscape]
    PF -- Back --> D
    P -- Back --> D
    D -- Back --> M
    M -- gear --> S[Settings]
    S -- How to Add Playlist --> H[Guides: URL / File / Stream]
    S -- Clear All Data --> CA[Confirm dialog]
    S -- Policy --> EXT[External web page]
    S -- Rate us / Share app --> X[External - not verified]
    H -- Back --> S
    S -- Back --> M
```
(Edges from Channel/Recent/Favorite cards to Player are inferred from the UI role "card = clickable"; only the Playlist Details → Player path was exercised.)

## 32. Unknown / Not Testable Features

```text
Status: Not Verified   (Reason: Could not be tested with available automation capabilities / would require changing system settings or destroying user data)
```
- Clean-install first-run experience (would require clearing the installed app's data) — the four starter playlists may or may not be seeded by the app.
- TRY IT NOW FREE (default-home) result, Setup Now result.
- Orientation of non-player screens; rotation during buffering; reboot persistence.
- Offline/airplane behaviour; throttled network; 403, timeouts, empty-body responses; HTTP (cleartext) stream playback.
- Cause of the Apple fMP4 HLS stream failing in-app while valid elsewhere.
- Why "MuxRenamed" did not appear in Recent; Recent size limit; duplicate collapsing; "clear history".
- Channel-tab/Recent/Favorite search; chip derivation; player ♡ effect; card long-press; tapping channel rows inside the player list; gesture controls (volume/brightness/seek); player controls auto-hide timing.
- Save in Rename Playlist; duplicate playlist handling; empty-name valid-URL validation.
- Rate us, Share app, Launcher Settings, Google folder, web search, news icon, dock icons.
- Android TV / DPAD / cast / external display.

**Test-data hygiene (for your information):** playlists `RnDFile`, `Comedy`, `T404`, my favorite, and my Recent entries were created during tests and then deleted/removed; the pushed `rnd_test_file.m3u` and temporary reverse-port were removed. The app list is back to Family / Entertainment / Movies / Sports. Ad redirects during automation opened Chrome, Brave and a Play Store sheet; nothing was installed and no settings were changed (default home verified unchanged).

## 33. Development Requirements

Target stack: **Android, Kotlin, XML, MVVM, ViewBinding, Room, Coroutines, StateFlow, RecyclerView, Media3 ExoPlayer.** Original UI/colors/typography/icons/illustrations/layouts/package name/implementation — **do not copy** proprietary assets.

### 33.1 Scope of parity (only confirmed behaviour)
Implement the confirmed functions: playlist import (https URL, file), playlist list with source filter, per-playlist channel list, channel favorite/rename/delete, search, Channel/Recent/Favorite tabs, player with confirmed controls, error handling, Settings (guide, clear data, support links), persistence.
**Do NOT implement** (not functional features / dark patterns): the default-launcher gate, the launcher-style shell home, any ad logic, the unused "Stream" guide option (not present in reference).

### 33.2 Data model (Room)
- `PlaylistEntity(id PK, name, sourceType {URL, FILE}, source /*url or file uri*/, createdAt)`
- `ChannelEntity(id PK, playlistId FK→Playlist ON DELETE CASCADE, name, groupTitle /*raw, "Unknown" default*/, logoUrl?, streamUrl, tvgId?, position)`
- `FavoriteEntity(streamUrl PK)` (keyed by stream URL; hearts across duplicates reflect same state; delete rows when no channel with that URL remains — reference removes favorites when playlist is deleted)
- `RecentEntity(streamUrl or channelSnapshot, playedAt)` — **do not cascade** on playlist delete (reference keeps orphans); store enough snapshot (name, group, logo, url) to render orphans. Insert on channel open, including failed streams; most recent first.
- Playlist channel count = rows in ChannelEntity for that playlist.

### 33.3 Parser rules (from §9)
1. Read lines; ignore `#EXTM3U`; on `#EXTINF` capture attributes (`tvg-id`, `tvg-name`, `tvg-logo`, `group-title`) and title after the last comma.
2. Next non-comment line is the stream URL → emit channel. **If the next line is another `#EXTINF`, drop the first entry.**
3. Name = `tvg-name` else title (may be blank → keep blank). Group = `group-title` else `"Unknown"`; keep `;` strings raw (optionally split for chips).
4. Keep duplicates and non-http schemes. Accept Unicode. No content-type/“is this M3U” validation: non-M3U body yields 1 blank channel (match reference or improve and document the deviation).
5. Parse on `Dispatchers.IO`; show spinner overlay; success → toast "Playlist added successfully!"; any I/O/HTTP error → toast "Error loading playlist" and keep form.

### 33.4 Network
- HTTPS only for playlist import; cleartext HTTP must fail with the error toast (default Android 9+ network-security config).
- Follow redirects; 404/IO error → same error toast.

### 33.5 Screens & behaviours
- **Main**: top search (filters playlists by name – live, case-insensitive, contains), settings icon, chips All/URL/File, 2-column playlist grid with ✎/🗑, empty state ("No Playlist found" + "Add new Playlist +"), bottom nav Playlist/Channel/+/Recent/Favorite. `+` opens Add from any tab. Back on Main exits to previous (no launcher shell).
- **Add**: tabs Import Link/Upload File (shared name field), required markers, clear (×) buttons, 4 suggestion rows (radio, fill name+URL), file picker via `ActivityResultContracts.OpenDocument()` (`*/*`), **show file display name** (reference shows raw id — improvement), validation toasts exactly as §8.2 (`Please enter URL`, `Please choose a file`, `Error loading playlist`).
- **Details**: toolbar + search icon (inline bar: live, case-insensitive, contains on **channel name only**, blank list when empty; Back closes keyboard then search); 2-col grid; ♡ toggle; ✎ "Rename Channel" dialog (editable name, read-only URL + Copy, Cancel/Save); 🗑 confirm dialog → delete; tap → Player.
- **Channel tab**: all channels, chip row (All + categories), large cards with logo (Glide/Coil + placeholder), category, name, "Explore ›".
- **Recent tab**: cards, 🗑 → "Remove from Recent" confirm; persisted.
- **Favorite tab**: list; empty-state texts "No channels Favourite" / "Tap the favorite icon to add to your favorite."
- **Settings**: How to Add Playlist (guide with URL/File tabs), Clear All Data (confirm → wipe playlists+recents+favorites), Rate us, Share app, Policy (open external URL).

### 33.6 Player (Media3)
- Layout: toolbar (back, playlist name, "n/total Channel"), 16:9 `PlayerView` with custom controller (♡, lock, prev, play/pause, next, "● Live" badge, fullscreen), spinner while buffering, channel list below (current highlighted, ♡ per row).
- No seekbar/volume/subtitle/audio/quality/speed/PiP/cast UI (not in reference).
- Controls: toggle on tap; auto-hide after ~3 s; lock hides everything except lock icon (unlock via lock icon).
- Error: Media3 `onPlayerError` → dialog with message "This channel is currently unavailable in your region. Please try again later." [Cancel → close player] [Next Channel → drop failed channel from the **session** list only, play next, update counter `n/total-1`].
- Fullscreen: switch to landscape + immersive-ish layout hiding toolbar/list; Back returns to Details.
- Insert Recent row when a channel is opened (also on failure).

### 33.7 Persistence & state
- Playlists, channels, favorites, recents survive process death. Do not restore last screen or last channel.
- Use `ViewModel + StateFlow` per screen; repository over Room; one-shot events (toasts/navigation) via `SharedFlow`/`Channel`.

### 33.8 Acceptance checklist (derived from verified behaviour)
- [ ] Import file with 10 EXTINF → 9 channels; names/categories per §9.
- [ ] `http://` URL import → "Error loading playlist".
- [ ] Details search `BIP` matches by name only; category/URL typed text yields blank list.
- [ ] Favoriting duplicate-URL channel marks both; Favorite tab shows one.
- [ ] Delete playlist removes its favorites but not its recents.
- [ ] Failed stream → dialog; Next Channel changes counter 2/8 → 3/7 and does not delete from DB.
- [ ] Fullscreen → landscape; Back → Details.
- [ ] Force-stop/relaunch keeps playlists, favorites, recents.

---
*End of document.* Anything not listed above as verified must be treated as **Status: Not Verified** and decided by the product owner before implementation.
